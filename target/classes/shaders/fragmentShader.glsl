#version 330

#define NR_POINT_LIGHTS 4

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

    vec3 ambient;
    vec3 diffuse;
    vec3 specular;

    float constant;
    float linear;
    float quadratic;
};

struct FlashLight {
    vec3  position;
    vec3  direction;
    float cutoff;
    float outerCutoff;

    vec3 ambient;
    vec3 diffuse;
    vec3 specular;

    float constant;
    float linear;
    float quadratic;
};

in vec3 normal;
in vec3 fragPos;
in vec2 texCoord;

out vec4 FragColor;

uniform Material material;
uniform DirectionalLight directionalLight;
uniform PointLight pointLights[NR_POINT_LIGHTS];
uniform FlashLight flashLight;

uniform vec3 viewerPosition;

vec3 CalculateDirectionalLight(DirectionalLight light, vec3 normal, vec3 viewDirection);
vec3 CalculatePointLight(PointLight light, vec3 normal, vec3 fragPos, vec3 viewDirection);
vec3 CalculateFlashLight(FlashLight light, vec3 normal, vec3 fragPos, vec3 viewDirection);

void main() {
    vec3 norm = normalize(normal);
    vec3 viewDirection = normalize(viewerPosition - fragPos);

    vec3 result = CalculateDirectionalLight(directionalLight, norm, viewDirection);

    for (int i = 0; i < NR_POINT_LIGHTS; i++) {
        result += CalculatePointLight(pointLights[i], norm, fragPos, viewDirection);
    }

    result += CalculateFlashLight(flashLight, norm, fragPos, viewDirection);

    result += directionalLight.ambient * vec3(texture(material.diffuse, texCoord));

    FragColor = vec4(result, 1);

    /*
    vec3 lightDirection = normalize(light.position - fragPos);

    float theta = dot(lightDirection, -light.direction);
    float epsilon = light.cutoff - light.outerCutoff;
    float intensity = clamp((theta - light.outerCutoff) / epsilon, 0.0, 1.0);

    vec3 ambient = light.ambient * vec3(texture(material.diffuse, texCoord));

    float diffuseCoefficient = max(dot(norm, lightDirection), 0);
    vec3 diffuse = light.diffuse * diffuseCoefficient * vec3(texture(material.diffuse, texCoord));

    vec3 viewDirection = normalize(viewerPosition - fragPos);
    vec3 reflectDirection = reflect(-lightDirection, norm);
    float specularCoefficient = pow(max(dot(viewDirection, reflectDirection), 0), material.shininess);
    vec3 specular = light.specular * specularCoefficient * vec3(texture(material.specular, texCoord));

    float distance = length(light.position - fragPos);
    float attenuation = 1.0 / (light.constant + light.linear * distance + light.quadratic * (distance * distance));

    vec3 result = intensity * attenuation * (diffuse + specular) + ambient;
    //FragColor = vec4(result, 1.0);
    FragColor = vec4(CalculateDirectionalLight(directionalLight, normal, viewDirection), 1);*/
}

vec3 CalculateDirectionalLight(DirectionalLight light, vec3 normal, vec3 viewDirection) {
    vec3 lightDir = normalize(-light.direction);
    float diffuseCoefficient = max(dot(lightDir, normal), 0);

    vec3 reflectedDirection = reflect(-lightDir, normal);
    float specularCoefficient = pow(max(dot(reflectedDirection, normal), 0), material.shininess);

    //vec3 ambient = light.ambient * vec3(texture(material.diffuse, texCoord));
    vec3 diffuse = diffuseCoefficient * light.diffuse * vec3(texture(material.diffuse, texCoord));
    vec3 specular = specularCoefficient * light.specular * vec3(texture(material.specular, texCoord));

    return (diffuse + specular);
}

vec3 CalculatePointLight(PointLight light, vec3 normal, vec3 fragPos, vec3 viewDirection) {
    vec3 lightDir = normalize(light.position - fragPos);
    float diffuseCoefficient = max(dot(normal, lightDir), 0);

    vec3 reflectDirection = reflect(-lightDir, normal);
    float specularCoefficient = pow(max(dot(viewDirection, reflectDirection), 0), material.shininess);

    float distance = length(light.position - fragPos);
    float attenuation = 1.0 / (light.constant + light.linear * distance + light.quadratic * (distance * distance));

    //vec3 ambient = light.ambient * vec3(texture(material.diffuse, texCoord));
    vec3 diffuse = diffuseCoefficient * light.diffuse * vec3(texture(material.diffuse, texCoord));
    vec3 specular = specularCoefficient * light.specular * vec3(texture(material.specular, texCoord));

    //ambient  *= attenuation;
    diffuse  *= attenuation;
    specular *= attenuation;
    return (diffuse + specular);
}

vec3 CalculateFlashLight(FlashLight light, vec3 normal, vec3 fragPos, vec3 viewDirection) {
    vec3 lightDir = normalize(light.position - fragPos);

    float theta = dot(lightDir, -light.direction);
    float epsilon = light.cutoff - light.outerCutoff;
    float intensity = clamp((theta - light.outerCutoff) / epsilon, 0.0, 1.0);

    //vec3 ambient = light.ambient * vec3(texture(material.diffuse, texCoord));
    float diffuseCoefficient = max(dot(normal, lightDir), 0);

    vec3 reflectDirection = reflect(-lightDir, normal);
    float specularCoefficient = pow(max(dot(viewDirection, reflectDirection), 0), material.shininess);

    float distance = length(light.position - fragPos);
    float attenuation = 1.0 / (light.constant + light.linear * distance + light.quadratic * (distance * distance));

    vec3 diffuse = light.diffuse * diffuseCoefficient * vec3(texture(material.diffuse, texCoord));
    vec3 specular = light.specular * specularCoefficient * vec3(texture(material.specular, texCoord));

    return (intensity * attenuation * (diffuse + specular));
}