package ru.youngsmoke.protection;

import java.io.*;

public class Protection {
 /*   static {
        String osName = System.getProperty("os.name").toLowerCase();
        String osArch = System.getProperty("os.arch").toLowerCase();

        String arch = switch (osArch) {
            case "x86_64", "amd64" -> "x64";
            case "aarch64" -> "arm64";
            case "arm" -> "arm32";
            case "x86" -> "x86";
            default -> throw new UnsupportedOperationException("Unsupported architecture: " + osArch);
        };
        String libName = switch (osName) {
            case String name when name.contains("win") -> "windows-" + arch + ".dll";
            case String name when name.contains("mac") -> "macos-" + arch + ".dylib";
            case String name when name.contains("nix") || name.contains("nux") || name.contains("aix") -> "linux-" + arch + ".so";
            default -> throw new UnsupportedOperationException("Unsupported OS: " + osName);
        };
        try (InputStream inputStream = Protection.class.getResourceAsStream("ru/youngsmoke/protection/libs/" + libName)) {
            if (inputStream == null) throw new UnsatisfiedLinkError("Library not found: " + libName);

            File libFile = File.createTempFile("lib", null);
            libFile.deleteOnExit();
            try (OutputStream outputStream = new FileOutputStream(libFile)) {
                inputStream.transferTo(outputStream);
            }
            System.load(libFile.getAbsolutePath());
        } catch (IOException e) {
            throw new UnsatisfiedLinkError("Failed to load library: " + e.getMessage());
        }
    }*/

    private static boolean initialized;

    static {
        if (!initialized) {
            initialized = true;
            String osName = System.getProperty("os.name").toLowerCase();
            String osArch = System.getProperty("os.arch").toLowerCase();
            String arch;
            switch (osArch) {
                case "x86_64":
                case "amd64":
                    arch = "x64";
                    break;
                case "aarch64":
                    arch = "arm64";
                    break;
                case "arm":
                    arch = "arm32";
                    break;
                case "x86":
                    arch = "x86";
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported architecture: " + osArch);
            }
            String libName;
            if (osName.contains("win")) {
                libName = "windows-" + arch + ".dll";
            } else if (osName.contains("mac")) {
                libName = "macos-" + arch + ".dylib";
            } else if (osName.contains("nix") || osName.contains("nux") || osName.contains("aix")) {
                libName = "linux-" + arch + ".so";
            } else {
                throw new UnsupportedOperationException("Unsupported OS: " + osName);
            }
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                inputStream = Protection.class.getResourceAsStream("/libs/" + libName);
                if (inputStream == null) {
                    throw new UnsatisfiedLinkError("Library not found: " + libName);
                }
                File libFile = File.createTempFile("lib", null);
                libFile.deleteOnExit();
                outputStream = new FileOutputStream(libFile);
                byte[] buffer = new byte[65536];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                System.load(libFile.getAbsolutePath());
                initialize();
            } catch (IOException e) {
                throw new UnsatisfiedLinkError("Failed to load library: " + e.getMessage());
            } finally {
                try {
                    if (inputStream != null) inputStream.close();
                    if (outputStream != null) outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static native void initialize();
}
