#include "PoseServer.h"

#include <iostream>
#include <uv.h>

namespace VulkanPoseTracking
{
	namespace PoseServer {
		uv_loop_t* loop;
		uv_udp_t recv_socket;

		int RunServer(int port)
		{			
			loop = uv_default_loop();

			uv_udp_init(loop, &recv_socket);
			struct sockaddr_in recv_addr;
			uv_ip4_addr("0.0.0.0", port, &recv_addr);
			uv_udp_bind(&recv_socket, (const struct sockaddr*)&recv_addr, UV_UDP_REUSEADDR);
			return port;
		}
	}
}
