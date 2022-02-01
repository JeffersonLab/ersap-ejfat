#ifndef ERSAP_EJFAT_ASSEMBLE_ENGINE_HPP_
#define ERSAP_EJFAT_ASSEMBLE_ENGINE_HPP_

#include <string>

namespace ersap {
namespace ejfat {

class EjfatAssembleEngine
{
public:

    EjfatAssembleEngine();

    void process(char **userBuf, size_t *userBufLen,
                 unsigned short port, const char *listeningAddr,
                 bool noCopy);

    void parseConfigFile();

private:

    std::string interface;
    int port;

};

} // end namespace ejfat
} // end namespace ersap

#endif
