#include "ejfat_packetize_engine.hpp"
#include "ejfat_packetize.hpp"

#include <iostream>
#include <fstream>
#include <sstream>

namespace ersap {
namespace ejfat {


    EjfatPacketizeEngine::EjfatPacketizeEngine() {
        // Look for a local config file (packetizer.yaml)


    }

    void EjfatPacketizeEngine::parseConfigFile() {
        std::ifstream file("./packetizer.yaml");
        if (!file) {
            std::cout << "unable to open ./packetizer.yaml file";
            exit (-1);
        }

        std::string line;
        while (getline(file, line)) {
            std::replace(line.begin(), line.end(), ':', ' ');  // replace ':' by ' '
            std::stringstream ss(line);
            std::string key, val;
            ss >> key;
            if (key == "port") {
                ss >> port;
            }
            else if (key == "mtu") {
                ss >> mtu;
            }
            else if (key == "host") {
                ss >> host;
            }
            else if (key == "interface") {
                ss >> interface;
            }
        }
    }


    void EjfatPacketizeEngine::process(char *buffer, uint32_t bufLen,
                                       std::string & host, const std::string & interface,
                                       int mtu, unsigned short port, uint64_t tick)
    {
        std::cout << "EJFAT processing..." << std::endl;

        int err = sendBuffer(buffer, bufLen, host, interface, mtu, port, tick);
        if (err < 0) {
            fprintf(stderr, "Error sending packets\n");
            exit (-1);
        }
    }
} // end namespace ejfat
} // end namespace ersap
