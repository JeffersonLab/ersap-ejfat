#include "ejfat_assemble_engine.hpp"
#include "ejfat_assemble.hpp"

#include <iostream>
#include <fstream>
#include <sstream>

namespace ersap {
namespace ejfat {


    EjfatAssembleEngine::EjfatAssembleEngine() {
        // Look for a local config file (assembler.yaml)


    }

    void EjfatAssembleEngine::parseConfigFile() {
        std::ifstream file("./assembler.yaml");
        if (!file) {
            std::cout << "unable to open ./assembler.yaml file";
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


    void EjfatAssembleEngine::process(char **userBuf, size_t *userBufLen,
                                       unsigned short port, const char *listeningAddr,
                                       bool noCopy)
    {
        std::cout << "EJFAT assembling ..." << std::endl;
        //static int getBuffer(char** userBuf, int32_t *userBufLen, unsigned short port, char *listeningAddr, bool noCopy) {

        int err = getBuffer(userBuf, userBufLen, port, listeningAddr, noCopy);
        if (err < 0) {
            fprintf(stderr, "Error assembling packets, err = %d\n", err);
            exit (-1);
        }
    }
} // end namespace ejfat
} // end namespace ersap
