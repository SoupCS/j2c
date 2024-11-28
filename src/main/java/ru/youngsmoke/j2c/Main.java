package ru.youngsmoke.j2c;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import picocli.CommandLine;
import ru.youngsmoke.j2c.utils.managers.SetupManager;
import ru.youngsmoke.j2c.xml.Config;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class Main {
    public static void main(final String[] args) {
        SetupManager.init();
        System.exit(new CommandLine(new NativeObfuscatorRunner()).setCaseInsensitiveEnumValuesAllowed(true).execute(args));
    }

    @CommandLine.Command(name = "Jnic", mixinStandardHelpOptions = true, version = {"HypeStudio JNIC"})
    private static class NativeObfuscatorRunner implements Callable<Integer> {
        @CommandLine.Parameters(index = "0", description = "Jar file to transpile")
        private File input;

        @CommandLine.Parameters(index = "1", description = "Output directory")
        private String output;

        @CommandLine.Option(names = {"-c", "--config"}, defaultValue = "config.xml",
                description = "Config file")
        private File config;

        @CommandLine.Option(names = {"-l", "--libraries"}, description = "Directory for dependent libraries")
        private File librariesDirectory;

        @CommandLine.Option(names = {"-a", "--annotations"}, description = "Use annotations to ignore/include native obfuscation")
        private boolean useAnnotations;

        @Override
        public Integer call() throws Exception {

            System.out.println("Чтение входного файла " + this.input);
            System.out.println("Чтение конфигурации " + this.config.toPath());
            final StringBuilder stringBuilder = new StringBuilder();
            if (Files.exists(this.config.toPath())) {
                try (final BufferedReader br = Files.newBufferedReader(this.config.toPath())) {
                    String str;
                    while ((str = br.readLine()) != null) {
                        stringBuilder.append(str);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Serializer serializer = new Persister();
                Config configInfo = serializer.read(Config.class, stringBuilder.toString());
                final List<Path> libs = new ArrayList<Path>();
                if (this.librariesDirectory != null) {
                    Files.walk(this.librariesDirectory.toPath(), FileVisitOption.FOLLOW_LINKS).filter(f -> f.toString().endsWith(".jar") || f.toString().endsWith(".zip")).forEach(libs::add);
                }
                if (new File(this.output).isDirectory()) {
                    final File outFile = new File(this.output, this.input.getName());
                    if (outFile.exists()) {
                        outFile.renameTo(new File(this.output, this.input.getName() + ".BACKUP"));
                    }
                } else {
                    final File outFile = new File(this.output);
                    if (outFile.exists()) {
                        outFile.renameTo(new File(this.output + ".BACKUP"));
                    }
                }
                new NativeProcessor().process(this.input.toPath(), Paths.get(this.output), configInfo, libs, this.useAnnotations);
                return 0;
            }

            final Path path = Files.createFile(this.config.toPath(), (FileAttribute<?>[]) new FileAttribute[0]);
            stringBuilder.append("<j2c>\n" +
                    "\t<targets>\n" +
                    "\t\t<target>WINDOWS_X86_64</target>\n" +
                    "\t\t<!--<target>WINDOWS_AARCH64</target>-->\n" +
                    "\t\t<target>MACOS_X86_64</target>\n" +
                    "\t\t<!--<target>MACOS_AARCH64</target>-->\n" +
                    "\t\t<target>LINUX_X86_64</target>\n" +
                    "\t\t<!--<target>LINUX_AARCH64</target>-->\n" +
                    "\t</targets>\n" +
                    "\t<include>\n" +
                    "\t\t<!-- Match supports Ant style path matching? Match one character, * match multiple characters, * * match multiple paths -->\n" +
                    "\t\t<match className=\"**\" />\n" +
                    "\t\t<!--<match className=\"dev/jnic/web/**\" />-->\n" +
                    "\t\t<!--<match className=\"dev.jnic.service.**\" />-->\n" +
                    "\t</include>\n" +
                    "\t<exclude>\n" +
                    "\t\t<!--<match className=\"dev/jnic/Main\" methodName=\"main\" methodDesc=\"(\\[Ljava/lang/String;)V\"/>-->\n" +
                    "\t\t<!--<match className=\"dev.jnic.test.**\" />-->\n" +
                    "\t</exclude>\n" +
                    "</j2c>\n");
            Files.write(path, stringBuilder.toString().getBytes(StandardCharsets.UTF_8));
            return 0;
        }
    }
}
