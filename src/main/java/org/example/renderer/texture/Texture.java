package org.example.renderer.texture;

import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.stb.STBImage.*;

public class Texture {
    private final int id;
    private int boundSlot = -1;
    private final String type;

    public Texture(String imagePathString, String type) {
        id = glGenTextures();

        bind();

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            stbi_set_flip_vertically_on_load(true);
            ByteBuffer image = stbi_load(imagePathString, width, height, channels, 0);

            if (image == null)
                throw new RuntimeException("Couldn't open the image " + imagePathString);

            int channelType = switch (channels.get(0)) {
                case 3 -> GL_RGB;
                case 4 -> GL_RGBA;
                default -> throw new RuntimeException("Image " + imagePathString + " should have 3 or 4 channels");
            };

            if (channelType == GL_RGBA) {
                glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
                glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            }

            glTexImage2D(GL_TEXTURE_2D, 0, channelType, width.get(0), height.get(0), 0, channelType, GL_UNSIGNED_BYTE, image);
            glGenerateMipmap(GL_TEXTURE_2D);

            stbi_image_free(image);
        }

        unbind();

        this.type = type;
    }

    public void bind(int slot) {
        glActiveTexture(GL_TEXTURE0 + slot);
        glBindTexture(GL_TEXTURE_2D, id);
        boundSlot = slot;
    }

    public void bind() {
        bind(0);
    }

    public void unbind() {
        glActiveTexture(boundSlot);
        glBindTexture(GL_TEXTURE_2D, 0);
        boundSlot = -1;
    }

    public boolean isBound() {
        return boundSlot != -1;
    }

    public String type() { return type; }
}