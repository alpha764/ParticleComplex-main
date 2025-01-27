package com.example.particlecomplex.utils.file_utils;

import net.minecraft.client.Minecraft;
import java.io.File;
import java.io.IOException;

public class FileUtil {

    public static File getMinecraftDirectory() {
        return Minecraft.getInstance().gameDirectory;
    }

    public static File getPresentFolder() {
        return new File(getMinecraftDirectory(), "presentparticle");
    }

    public static File getFileInPresentParticleFolder(String filename) {
        return new File(getPresentFolder(), filename);
    }
    public static void deleteDirectoryRecursively(File dir) throws IOException {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    deleteDirectoryRecursively(file);
                }
            }
        }
        if (!dir.delete()) {
            throw new IOException("Failed to delete " + dir);
        }
    }
}
