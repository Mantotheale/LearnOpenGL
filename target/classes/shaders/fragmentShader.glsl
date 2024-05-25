#version 330 core
out vec4 FragColor;

struct Material {
    sampler2D diffuse;
    sampler2D specular;
    float shininess;
};

struct LightSource {
    vec3 position;
    vec3 direction;

    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
};

struct Attenuation {
    float constant;
    float linear;
    float quadratic;
};

struct DirectionalLight {
    LightSource source;
};

struct PointLight {
    LightSource source;
    Attenuation attenuation;
};

struct SpotLight {
    LightSource source;
    Attenuation attenuation;

    float cutoff;
    float outerCutoff;
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
float attenuationCoefficient(LightSource source, Attenuation attenuation);

vec3 sampleTexture(sampler2D tex, vec2 texCoord);
vec3 combineComponents(LightSource lightSource, float diff, float spec, sampler2D diffTex, sampler2D specTex, vec2 texCoord);

void main() {
    vec3 norm = normalize(normal);
    vec3 viewDir = normalize(viewerPosition - fragPos);

    vec3 result = CalcDirLight(directionalLight, norm, viewDir);

    for(int i = 0; i < NR_POINT_LIGHTS; i++)
        result += CalcPointLight(pointLights[i], norm, fragPos, viewDir);

    result += CalcSpotLight(spotLight, norm, fragPos, viewDir);

    result += directionalLight.source.ambient * sampleTexture(material.diffuse, texCoord);

    FragColor = vec4(result, 1.0);
}

vec3 CalcDirLight(DirectionalLight light, vec3 normal, vec3 viewDir) {
    vec3 lightDir = normalize(-light.source.direction);

    float diff = diffuseCoefficient(lightDir, normal);
    float spec = specularCoefficient(lightDir, normal, viewDir, material.shininess);

    return combineComponents(light.source, diff, spec, material.diffuse, material.specular, texCoord);
}

vec3 CalcPointLight(PointLight light, vec3 normal, vec3 fragPos, vec3 viewDir) {
    vec3 lightDir = normalize(light.source.position - fragPos);

    float diff = diffuseCoefficient(lightDir, normal);
    float spec = specularCoefficient(lightDir, normal, viewDir, material.shininess);

    float attenuation = attenuationCoefficient(light.source, light.attenuation);

    return attenuation * combineComponents(light.source, diff, spec, material.diffuse, material.specular, texCoord);
}

vec3 CalcSpotLight(SpotLight light, vec3 normal, vec3 fragPos, vec3 viewDir) {
    vec3 lightDir = normalize(light.source.position - fragPos);

    float diff = diffuseCoefficient(lightDir, normal);
    float spec = specularCoefficient(lightDir, normal, viewDir, material.shininess);

    float attenuation = attenuationCoefficient(light.source, light.attenuation);

    float theta = dot(lightDir, normalize(-light.source.direction));
    float epsilon = light.cutoff - light.outerCutoff;
    float intensity = clamp((theta - light.outerCutoff) / epsilon, 0.0, 1.0);

    return attenuation * intensity * combineComponents(light.source, diff, spec, material.diffuse, material.specular, texCoord);
}

float diffuseCoefficient(vec3 lightDir, vec3 normal) {
    return max(dot(normal, lightDir), 0.0);
}

float specularCoefficient(vec3 lightDir, vec3 normal, vec3 viewDirection, float shininess) {
    vec3 reflectDirection = reflect(-lightDir, normal);
    float specCoeff = pow(max(dot(viewDirection, reflectDirection), 0.0), shininess);

    return specCoeff;
}

float attenuationCoefficient(LightSource source, Attenuation attenuation) {
    float distance = length(source.position - fragPos);
    return 1.0 / (attenuation.constant + attenuation.linear * distance + attenuation.quadratic * (distance * distance));
}

vec3 sampleTexture(sampler2D tex, vec2 texCoord) {
    return vec3(texture(tex, texCoord));
}

vec3 combineComponents(LightSource lightSource, float diff, float spec, sampler2D diffTex, sampler2D specTex, vec2 texCoord) {
    vec3 diffuse = lightSource.diffuse * diff * sampleTexture(diffTex, texCoord);
    vec3 specular = lightSource.specular * spec * sampleTexture(specTex, texCoord);

    return diffuse + specular;
}