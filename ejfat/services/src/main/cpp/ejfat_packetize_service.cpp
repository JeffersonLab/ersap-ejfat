#include "ejfat_packetize_service.hpp"
#include "ejfat_packetize.hpp"

#include <ejfat_packetize_engine.hpp>

#include <ersap/stdlib/json_utils.hpp>
#include <ersap/engine_data.hpp>

#include <cmath>
#include <iostream>

#include <chrono>
#include <sys/time.h>
#include <ctime>
#include <atomic>
#include <cstddef>
#include <cstdint>


#if __APPLE__
    #define bswap_16(value) ((((value) & 0xff) << 8) | ((value) >> 8))

    #define bswap_32(value) \
    (((uint32_t)bswap_16((uint16_t)((value) & 0xffff)) << 16) | \
    (uint32_t)bswap_16((uint16_t)((value) >> 16)))

    #define bswap_64(value) \
    (((uint64_t)bswap_32((uint32_t)((value) & 0xffffffff)) << 32) | \
    (uint64_t)bswap_32((uint32_t)((value) >> 32)))
#else
    #include <byteswap.h>
#endif

#ifdef __APPLE__
#include <cctype>
#endif


extern "C"
std::unique_ptr<ersap::Engine> create_engine()
{
    return std::make_unique<ersap::ejfat::EjfatPacketizeService>();
}


namespace ersap {
namespace ejfat {

time_t start;

ersap::EngineData EjfatPacketizeService::configure(ersap::EngineData& input)
{
    // Ersap provides a simple JSON parser to read configuration data
    // and configure the service.
    auto config = ersap::stdlib::parse_json(input);

    start = time(nullptr);

    host = ersap::stdlib::get_string(config, "host");
    interface = ersap::stdlib::get_string(config, "interface");

    mtu = ersap::stdlib::get_int(config, "mtu");
    port = ersap::stdlib::get_int(config, "port");

    // Example for when the service has state that is configured by
    // the orchestrator. The "state" object should be a std::shared_ptr
    // accessed atomically.
    //
    // (This service is actually stateless, so detector_ could just simply be
    // initialized in the service constructor).
    std::atomic_store(&engine_, std::make_shared<EjfatPacketizeEngine>());
    return {};
}


ersap::EngineData EjfatPacketizeService::execute(ersap::EngineData& input)
{
/*
 auto output = ersap::EngineData{};
    time_t end = time(nullptr);

    if (end - start >= 10) {
    // Pull out needed items from data
    uint32_t *i = data_cast<uint32_t*>(input);
    uint64_t tick = ntohl(*i);
    uint32_t bufLen = ntohl(*(i+1));

    char *buffer = reinterpret_cast<char *>(i+2);

    // This always loads the shared_pointer into a new shared_ptr
    std::atomic_load(&engine_)->process(buffer, bufLen, host, interface, mtu, port, tick);
    start = end;
}
*/
    return input;
}

ersap::EngineData EjfatPacketizeService::execute_group(const std::vector<ersap::EngineData>&)
{
    return {};
}

std::vector<ersap::EngineDataType> EjfatPacketizeService::input_data_types() const
{
    return { ersap::type::JSON, ersap::type::BYTES };
}


std::vector<ersap::EngineDataType> EjfatPacketizeService::output_data_types() const
{
    return { ersap::type::BYTES };
}


std::set<std::string> EjfatPacketizeService::states() const
{
    return std::set<std::string>{};
}


std::string EjfatPacketizeService::name() const
{
    return "EjfatPacketizeService";
}


std::string EjfatPacketizeService::author() const
{
    return "Carl Timmer";
}


std::string EjfatPacketizeService::description() const
{
    return "EJFAT service to UDP packetize data and sent to FPGA-based load balancer";
}


std::string EjfatPacketizeService::version() const
{
    return "0.1";
}

}
}
