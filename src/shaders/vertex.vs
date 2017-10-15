#version 330

layout (location=0) in vec3 position;
layout (location=1) in vec3 color;

out vec3 vertexColor;

uniform mat4 modelView;
uniform mat4 projection;

void main(){
    gl_Position = projection * modelView * vec4(position, 1.0);
    vertexColor = color;
}