package ru.youngsmoke.j2c;

import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import ru.youngsmoke.j2c.compiler.MethodCompiler;
import ru.youngsmoke.j2c.preprocessors.PreprocessorRunner;
import ru.youngsmoke.j2c.utils.Util;
import ru.youngsmoke.j2c.utils.classes.ClassMetadataReader;
import ru.youngsmoke.j2c.utils.classes.ClassMethodFilter;
import ru.youngsmoke.j2c.utils.classes.SafeClassWriter;
import ru.youngsmoke.j2c.xml.Config;
import ru.youngsmoke.j2c.xml.Match;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class NativeProcessor {
    public static ClassMethodFilter classMethodFilter;

    public static void moveDirectoryFromCurrentJar(ZipOutputStream zos, String directory) throws IOException {
        String currentJarPath = new File(
                Objects.requireNonNull(
                        NativeProcessor.class.getProtectionDomain().getCodeSource().getLocation()
                ).getPath()
        ).getAbsolutePath();

        try (JarFile jar = new JarFile(currentJarPath)) {
            jar.stream()
                    .filter(entry -> entry.getName().startsWith(directory) && !entry.isDirectory())
                    .forEach(entry -> {
                        try {
                            ZipEntry newEntry = new ZipEntry(entry.getName());
                            zos.putNextEntry(newEntry);

                            try (InputStream is = jar.getInputStream(entry)) {
                                is.transferTo(zos);
                            }
                            zos.closeEntry();
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    });
        }
    }

    public static boolean shouldProcess(MethodNode method) {

        return !Util.getFlag(method.access, 1024) && !Util.getFlag(method.access, 256) && !method.name.equals("<init>");
    }

    public void process(Path inputJarPath, Path outputJarPath, Config config, List<Path> inputLibs, boolean useAnnotations) throws IOException {
        //SnippetsTest snippets = new SnippetsTest();

        MethodCompiler methodCompiler = new MethodCompiler();
        //TODO START: чтение конфига
        Path outputDir;
        ArrayList<Path> libs = new ArrayList<>(inputLibs);
        libs.add(inputJarPath);
        ArrayList<String> whiteList = new ArrayList<>();
        if (config.getIncludes() != null) {
            for (Match match : config.getIncludes()) {
                StringBuilder stringBuilder = new StringBuilder();
                if (StringUtils.isNotEmpty(match.getClassName())) {
                    stringBuilder.append(match.getClassName().replaceAll("\\.", "/"));
                }
                if (StringUtils.isNotEmpty(match.getMethodName())) {
                    stringBuilder.append("#").append(match.getMethodName());
                    if (StringUtils.isNotEmpty(match.getMethodDesc())) {
                        stringBuilder.append("!").append(match.getMethodDesc());
                    }
                } else if (StringUtils.isNotEmpty(match.getMethodDesc())) {
                    stringBuilder.append("#**!").append(match.getMethodDesc());
                }
                whiteList.add(stringBuilder.toString());
            }
        }
        ArrayList<String> blackList = new ArrayList<>();
        if (config.getExcludes() != null) {
            for (Match include : config.getExcludes()) {
                StringBuilder stringBuilder = new StringBuilder();
                if (StringUtils.isNotEmpty(include.getClassName())) {
                    stringBuilder.append(include.getClassName().replaceAll("\\.", "/"));
                }
                if (StringUtils.isNotEmpty(include.getMethodName())) {
                    stringBuilder.append("#").append(include.getMethodName());
                    if (StringUtils.isNotEmpty(include.getMethodDesc())) {
                        stringBuilder.append("!").append(include.getMethodDesc());
                    }
                } else if (StringUtils.isNotEmpty(include.getMethodDesc())) {
                    stringBuilder.append("#**!").append(include.getMethodDesc());
                }
                blackList.add(stringBuilder.toString());
            }
        }
        classMethodFilter = new ClassMethodFilter(blackList, whiteList, useAnnotations);
        ClassMetadataReader metadataReader = new ClassMetadataReader(libs.stream().map(x -> {
            try {
                return new JarFile(x.toFile());
            } catch (IOException ex) {
                return null;
            }
        }).collect(Collectors.toList()));
        //TODO END: чтение конфига

        //TODO START: Обработка выходного файла
        String outputName = inputJarPath.getFileName().toString();

        if (!outputJarPath.toFile().exists()) {
            Files.createDirectory(outputJarPath);
        }

        if (outputJarPath.toFile().isDirectory()) {
            outputDir = outputJarPath;
        } else {
            outputDir = outputJarPath.getParent();
            outputName = outputJarPath.getFileName().toString();
        }
        //TODO END: Обработка выходного файла


        //TODO START: создание темп файлов и перенос доп файлов из ресурсов
        Path cppDir = outputDir.resolve("cpp");
        Files.createDirectories(cppDir);
        Util.copyResource("jni.h", cppDir);
        HashMap<String, ClassNode> map = new HashMap<>();
        HashMap<String, String> classNameMap = new HashMap<>();
        StringBuilder instructions = new StringBuilder(); // список который будет заполнен уже С++ кодом
        File jarFile = inputJarPath.toAbsolutePath().toFile();
        Path temp = Files.createTempDirectory("native");
        Path tempFile = temp.resolve(UUID.randomUUID() + ".data");
        //TODO END: создание темп файлов и перенос доп файлов из ресурсов
        BufferedWriter mainWriter = Files.newBufferedWriter(cppDir.resolve("dllmain.cpp"));
        //TODO START: создаётся выходной файл и его наполнение с обработкой входного файла
        try (ZipOutputStream out = new ZipOutputStream(Files.newOutputStream(outputDir.resolve(outputName)));
             ZipOutputStream source = new ZipOutputStream(Files.newOutputStream(tempFile))) {

            JarFile jar = new JarFile(jarFile);
            //moveDirectoryFromCurrentJar(out, "ru/youngsmoke/protection/");
            jar.stream().forEach(entry -> {

                if (entry.getName().equals("META-INF/MANIFEST.MF")) {
                    return;
                }

                try {
                    if (!entry.getName().endsWith(".class")) {
                        //   System.out.println("1: " + entry.getName());
                        Util.writeEntry(jar, out, entry);
                        return;
                    }
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    try (InputStream in = jar.getInputStream(entry)) {
                        Util.transfer(in, baos);
                    }
                    byte[] src = baos.toByteArray();

                    if (Util.byteArrayToInt(Arrays.copyOfRange(src, 0, 4)) != 0xCAFEBABE) {
                        Util.writeEntry(out, entry.getName(), src);
                        return;
                    }

                    ClassReader classReader = new ClassReader(src);
                    ClassNode rawClassNode = new ClassNode(458752);
                    classReader.accept(rawClassNode, 2);
                    if (!classMethodFilter.shouldProcess(rawClassNode) || rawClassNode.methods.stream().noneMatch(method -> shouldProcess(method) && classMethodFilter.shouldProcess(rawClassNode, method))) {

                        Util.writeEntry(out, entry.getName(), src);
                        return;
                    }



       /*             rawClassNode.methods.stream()
                            .filter(NativeProcessor::shouldProcess)
                            .filter(methodNode -> classMethodFilter.shouldProcess(rawClassNode, methodNode))
                            .forEach(methodNode -> PreprocessorRunner.preprocess(rawClassNode));
*/
                    // добавка в классинит для вызова лоадера
                    if (rawClassNode.methods.stream().noneMatch(x -> x.name.equals("<clinit>"))) {

                        rawClassNode.methods.add(new MethodNode(458752, 8, "<clinit>", "()V", null, new String[0]));
                    }

                    PreprocessorRunner.preprocess(rawClassNode);

                    Util.writeEntry(jar, source, entry);
                    // эта хуйня нужна чтобы пересчитать maxStack для правильной обработки байткода если не пересчитать стек то методпроцессор не досчитает стэк
                    SafeClassWriter preprocessorClassWriter = new SafeClassWriter(metadataReader, 458755);
                    rawClassNode.accept(preprocessorClassWriter);
                    classReader = new ClassReader(preprocessorClassWriter.toByteArray());
                    ClassNode classNode = new ClassNode(458752);
                    classReader.accept(classNode, 0);


                    instructions.append("\n//").append(classNode.name).append("\n");

                    ClassWriter writer = new ClassWriter();
                    classNode.methods.stream()
                            .filter(methodNode -> (!methodNode.name.equalsIgnoreCase("<clinit>") && !methodNode.name.equalsIgnoreCase("<init>")))
                            .forEach(method -> /*{System.out.println(method.name);}*/methodCompiler.process(method, writer));
                    instructions.append(writer.output);
                    SafeClassWriter classWriter = new SafeClassWriter(metadataReader, 458755);
                    classNode.accept(classWriter);
                    Util.writeEntry(out, entry.getName(), classWriter.toByteArray());


                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
            mainWriter.append(instructions);
            mainWriter.close();
            source.flush();
            source.close();


            // TODO: loader


            Manifest mf = jar.getManifest();
            if (mf != null) {
                out.putNextEntry(new ZipEntry(JarFile.MANIFEST_NAME));
                mf.write(out);
            }


            out.closeEntry();

            metadataReader.close();
        }
        //TODO END: создаётся выходной файл и его наполнение с обработкой входного файла

    }

}
