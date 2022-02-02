#ifndef ERSAP_EJFAT_PACKETIZE_ENGINE_HPP_
#define ERSAP_EJFAT_PACKETIZE_ENGINE_HPP_

#include <string>

namespace ersap {
namespace ejfat {

class EjfatPacketizeEngine
{
public:

    EjfatPacketizeEngine();

    void process(char *buffer, uint32_t bufLen,
                 std::string & host, const std::string & interface,
                 int mtu, unsigned short port, uint64_t tick,
                 int version, int dataId, bool debug);

    void parseConfigFile();

private:

    std::string host;
    std::string interface;

    int mtu;
    int port;
    int version;
    int dataId;

    bool debug;

};

} // end namespace ejfat
} // end namespace ersap

#endif
