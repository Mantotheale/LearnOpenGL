#version 330 core
out vec4 FragColor;

struct Material {
    sampler2D diffuse;
    sampler2D specular;
    float shininess;
};

struct DirectionalLight {
    vec3 direction;

    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
};

struct PointLight {
    vec3 position;

    float constant;
    float linear;
    float quadratic;

    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
};

struct SpotLight {
    vec3 position;
    vec3 direction;
    float cutoff;
    float outerCutoff;

    float constant;
    float linear;
    float quadratic;

    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
};

struct Light {
    vec3 position;
    vec3 direction;

    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
};

#define NR_POINT_LIGHTS 4

in vec3 fragPos;
in vec3 normal;
in vec2 texCoord;

uniform vec3 viewerPosition;
uniform DirectionalLight directionalLight;
uniform PointLight pointLights[NR_POINT_LIGHTS];
uniform SpotLight spotLight;
uniform Material material;

// function prototypes
vec3 CalcDirLight(DirectionalLight light, vec3 normal, vec3 viewDir);
vec3 CalcPointLight(PointLight light, vec3 normal, vec3 fragPos, vec3 viewDir);
vec3 CalcSpotLight(SpotLight light, vec3 normal, vec3 fragPos, vec3 viewDir);

float diffuseCoefficient(vec3 lightDir, vec3 normal);
float specularCoefficient(vec3 lightDir, vec3 normal, vec3 viewDirection, float shininess);

vec3 sampleTexture(sampler2D tex, vec2 texCoord);
vec3 combineComponents(vec3 lightAmbient, vec3 lightDiffuse, vec3 lightSpecular, float diff, float spec, sampler2D diffTex, sampler2D specTex, vec2 texCoord);

void main() {
    vec3 norm = normalize(normal);
    vec3 viewDir = normalize(viewerPosition - fragPos);

    vec3 result = CalcDirLight(directionalLight, norm, viewDir);

    for(int i = 0; i < NR_POINT_LIGHTS; i++)
        result += CalcPointLight(pointLights[i], norm, fragPos, viewDir);

    //result += CalcSpotLight(spotLight, norm, fragPos, viewDir);

    FragColor = vec4(result, 1.0);
}

// calculates the color when using a directional light.
vec3 CalcDirLight(DirectionalLight light, vec3 normal, vec3 viewDir) {
    vec3 lightDir = normalize(-light.direction);

    float diff = diffuseCoefficient(lightDir, normal);
    float spec = specularCoefficient(lightDir, normal, viewDir, material.shininess);

    return combineComponents(light.ambient, light.diffuse, light.specular, diff, spec, material.diffuse, material.specular, texCoord);
}

// calculates the color when using a point light.
vec3 CalcPointLight(PointLight light, vec3 normal, vec3 fragPos, vec3 viewDir) {
    vec3 lightDir = normalize(light.position - fragPos);

    float diff = diffuseCoefficient(lightDir, normal);
    float spec = specularCoefficient(lightDir, normal, viewDir, material.shininess);

    // attenuation
    float distance = length(light.position - fragPos);
    float attenuation = 1.0 / (light.constant + light.linear * distance + light.quadratic * (distance * distance));

    return attenuation * combineComponents(light.ambient, light.diffuse, light.specular, diff, spec, material.diffuse, material.specular, texCoord);
}

// calculates the color when using a spot light.
vec3 CalcSpotLight(SpotLight light, vec3 normal, vec3 fragPos, vec3 viewDir) {
    vec3 lightDir = normalize(light.position - fragPos);

    float diff = diffuseCoefficient(lightDir, normal);
    float spec = specularCoefficient(lightDir, normal, viewDir, material.shininess);

    // attenuation
    float distance = length(light.position - fragPos);
    float attenuation = 1.0 / (light.constant + light.linear * distance + light.quadratic * (distance * distance));
    // spotlight intensity
    float theta = dot(lightDir, normalize(-light.direction));
    float epsilon = light.cutoff - light.outerCutoff;
    float intensity = clamp((theta - light.outerCutoff) / epsilon, 0.0, 1.0);
    // combine results

    return attenuation * combineComponents(light.ambient, light.diffuse, light.specular, diff, spec, material.diffuse, material.specular, texCoord);
}

float diffuseCoefficient(vec3 lightDir, vec3 normal) {
    return max(dot(normal, lightDir), 0.0);
}

float specularCoefficient(vec3 lightDir, vec3 normal, vec3 viewDirection, float shininess) {
    vec3 reflectDirection = reflect(-lightDir, normal);
    float specCoeff = pow(max(dot(viewDirection, reflectDirection), 0.0), shininess);

    return specCoeff;
}

vec3 sampleTexture(sampler2D tex, vec2 texCoord) {
    return vec3(texture(tex, texCoord));
}

vec3 combineComponents(vec3 lightAmbient, vec3 lightDiffuse, vec3 lightSpecular, float diff, float spec, sampler2D diffTex, sampler2D specTex, vec2 texCoord) {
    vec3 ambient = lightAmbient * sampleTexture(diffTex, texCoord);
    vec3 diffuse = lightDiffuse * diff * sampleTexture(diffTex, texCoord);
    vec3 specular = lightSpecular * spec * sampleTexture(specTex, texCoord);

    return ambient + diffuse + specular;
}
