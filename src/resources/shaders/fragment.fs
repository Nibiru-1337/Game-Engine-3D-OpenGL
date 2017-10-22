#version 330

in vec3 vertexColor;
out vec4 fragColor;

uniform vec3 color;
uniform int useColor;

void main() {
    if ( useColor == 1 ){
        fragColor = vec4(color, 1);
    }
    else{
        fragColor = vec4(0.0f, 0.0f, 0.0f, 1);
        //fragColor = texture(texture_sampler, outTexCoord);
    }
};