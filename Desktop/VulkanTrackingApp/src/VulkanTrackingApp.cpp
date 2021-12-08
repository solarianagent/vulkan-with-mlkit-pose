// VulkanTrackingApp.cpp : Defines the entry point for the application.
//

#include "PoseServer/PoseServer.h"

int main()
{
	return VulkanPoseTracking::PoseServer::RunServer(68);
}
