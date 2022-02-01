#include "ejfat_packetize_engine.hpp"
#include "ejfat_packetize.hpp"

#include <iostream>
#include <fstream>
#include <sstream>
#include <stdlib.h>
#include <vector>
#include <iterator>
#include <string>
#include <cctype>
#include <errno.h>

#ifdef __APPLE__
#include <inttypes.h>
#endif

namespace ersap {
namespace ejfat {


    EjfatPacketizeEngine::EjfatPacketizeEngine() {
        // Default values
        port = 19522;
        mtu  = 1024;
        host = "127.0.0.1";
        interface = "eth0";
    }


    static std::vector<std::string> split(const std::string &s, char delim)
    {
        std::stringstream ss(s);
        std::string item;
        std::vector<std::string> elems;
        while (std::getline(ss, item, delim)) {
            elems.push_back(item);
        }
        return elems;
    }

    static std::string trim(const std::string &s)
    {
        auto start = s.begin();
        while (start != s.end() && std::isspace(*start)) {
            start++;
        }

        auto end = s.end();
        do {
            end--;
        } while (std::distance(start, end) > 0 && std::isspace(*end));

        return std::string(start, end + 1);
    }


    void EjfatPacketizeEngine::parseConfigFile()
    {
        std::ifstream file("./packetizer.yaml");
        if (!file) {
            std::cout << "unable to open ./packetizer.yaml file, use default values";
            return;
        }

        std::string line;
        while (getline(file, line)) {
            // Split at ":"
            std::vector<std::string> strs = split(line, ':');
            // Strip off white space
            const std::string key = trim(strs[0]);
            const std::string val = trim(strs[1]);

            if (key == "port") {
                port = (int)strtol(val.c_str(), (char **)nullptr, 10);
                if ((port == 0) && (errno == EINVAL || errno == ERANGE)) {
                    port = 19522;
                }
            }
            else if (key == "mtu") {
                mtu = (int)strtol(val.c_str(), (char **)nullptr, 10);
                if ((mtu == 0) && (errno == EINVAL || errno == ERANGE)) {
                    mtu = 1024;
                }
            }
            else if (key == "host") {
                host = val;
            }
            else if (key == "interface") {
                interface = val;
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
