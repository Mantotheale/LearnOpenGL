package org.example.renderer;

import org.example.renderer.buffer.Vertex;
import org.example.renderer.shader.ShaderProgram;
import org.example.renderer.texture.Texture;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.assimp.*;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;
import java.util.*;

import static org.lwjgl.assimp.Assimp.*;

public class Model {
    private final List<Mesh> meshes = new ArrayList<>();
    private String directory;

    public Model(String filePathString) {
        loadModel(filePathString);
    }

    public void draw(ShaderProgram shader) {
        for (Mesh mesh: meshes)
            mesh.draw(shader);
    }

    private void loadModel(String filePathString) {
        try (AIScene scene = Assimp.aiImportFile(filePathString, Assimp.aiProcess_Triangulate | Assimp.aiProcess_FlipUVs)) {
            directory = filePathString.substring(0, filePathString.lastIndexOf('/'));

            if (scene == null || scene.mRootNode() == null || (scene.mFlags() & AI_SCENE_FLAGS_INCOMPLETE) != 0)  {
                throw new RuntimeException("ERROR::ASSIMP:: " + Assimp.aiGetErrorString());
            }

            processNode(Objects.requireNonNull(scene.mRootNode()), scene);
        }
    }

    private void processNode(AINode node, AIScene scene) {
        for(int i = 0; i < node.mNumMeshes(); i++) {
            AIMesh mesh = AIMesh.create(scene.mMeshes().get(node.mMeshes().get(i)));
            meshes.add(processMesh(mesh, scene));
        }

        for(int i = 0; i < node.mNumChildren(); i++) {
            processNode(AINode.create(node.mChildren().get(i)), scene);
        }
    }

    private Mesh processMesh(AIMesh mesh, AIScene scene) {
        List<Vertex> vertices = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        List<Texture> textures = new ArrayList<>();

        for(int i = 0; i < mesh.mNumVertices(); i++) {
            AIVector3D meshVertex = mesh.mVertices().get(i);
            AIVector3D meshNormal = mesh.mNormals().get(i);

            Vector3f position = new Vector3f(meshVertex.x(), meshVertex.y(), meshVertex.z());
            Vector3f normal = new Vector3f(meshNormal.x(), meshNormal.y(), meshNormal.z());
            Vector2f texCoord;

            AIVector3D.Buffer tex = mesh.mTextureCoords(0);
            if (tex != null) {
                texCoord = new Vector2f(tex.get(i).x(), tex.get(i).y());
            } else {
                texCoord = new Vector2f();
            }

            vertices.add(new Vertex(position, normal, texCoord));
        }

        for(int i = 0; i < mesh.mNumFaces(); i++) {
            AIFace face = mesh.mFaces().get(i);

            for(int j = 0; j < face.mNumIndices(); j++)
                indices.add(face.mIndices().get(j));
        }

        if(mesh.mMaterialIndex() >= 0) {
            AIMaterial material = AIMaterial.create(scene.mMaterials().get(mesh.mMaterialIndex()));

            List<Texture> diffuseMaps = loadMaterialTextures(material, Assimp.aiTextureType_DIFFUSE, "texture_diffuse");
            textures.addAll(diffuseMaps);

            List<Texture> specularMaps = loadMaterialTextures(material, Assimp.aiTextureType_SPECULAR, "texture_specular");
            textures.addAll(specularMaps);
        }

        return new Mesh(vertices, indices, textures);
    }

    Map<String, Texture> loadedTextures = new HashMap<>();

    List<Texture> loadMaterialTextures(AIMaterial material, int type, String typeName) {
        List<Texture> textures = new ArrayList<>();

        AIString path = new AIString(MemoryUtil.memAlloc(1028));
        for(int i = 0; i < aiGetMaterialTextureCount(material, type); i++) {
            aiGetMaterialTexture(material, type, i, path, (IntBuffer) null, null, null, null, null, null);

            if (loadedTextures.containsKey(path.dataString())) {
                textures.add(loadedTextures.get(path.dataString()));
            } else {
                Texture texture = new Texture(directory + "/" + path.dataString(), typeName);
                textures.add(texture);
                loadedTextures.put(path.dataString(), texture);
            }
        }

        return textures;
    }
}
