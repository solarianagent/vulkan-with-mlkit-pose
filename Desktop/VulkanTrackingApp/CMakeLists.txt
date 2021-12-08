# CMakeList.txt : CMake project for VulkanTrackingApp, include source and define
# project specific logic here.
#
cmake_minimum_required (VERSION 3.8)

project ("VulkanTrackingApp")

add_subdirectory("dependencies/libuv")


# Add source to this project's executable.
add_executable (VulkanTrackingApp "src/VulkanTrackingApp.cpp" "src/VulkanTrackingApp.h")

# TODO: Add tests and install targets if needed.
target_link_libraries(VulkanTrackingApp uv_a)
