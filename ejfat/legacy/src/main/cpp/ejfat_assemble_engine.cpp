#include "ejfat_assemble_engine.hpp"
#include "ejfat_assemble.hpp"

#include <cmath>
#include <iostream>
#include <fstream>
#include <sstream>

namespace ersap {
namespace ejfat {


    EjfatAssembleEngine::EjfatAssembleEngine() {
        // Look for a local config file (assemble.yaml)


    }

    void EjfatAssembleEngine::parseConfigFile() {
        std::ifstream file("./assemble.yaml");
        if (!file) {
            std::cout << "unable to open ./assemble.yaml file";
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
            else if (key == "interface") {
                ss >> interface;
            }
        }
    }


    void EjfatAssembleEngine::process(char *buffer, uint32_t bufLen,
                                       std::string & host, std::string & interface,
                                       int mtu, unsigned short port, uint64_t tick)
    {
        std::cout << "EJFAT processing..." << std::endl;

        //sendBuffer(buffer, bufLen, host, interface,
        //           mtu, port, tick);
    }
} // end namespace ejfat
} // end namespace ersap
