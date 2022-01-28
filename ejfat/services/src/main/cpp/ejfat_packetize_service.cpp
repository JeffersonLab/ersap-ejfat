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
    start = time(nullptr);

    // Ersap provides a simple JSON parser to read configuration data
    // and configure the service.
    auto config = ersap::stdlib::parse_json(input);

    std::string  host = ersap::stdlib::get_string(config, "host");
    std::string interface = ersap::stdlib::get_string(config, "interface");

    int mtu = ersap::stdlib::get_int(config, "mtu");
    int port = ersap::stdlib::get_int(config, "port");

    // Example for when the service has state that is configured by
    // the orchestrator. The "state" object should be a std::shared_ptr
    // accessed atomically.
    //
    // (This service is actually stateless, so detector_ could just simply be
    // initialized in the service constructor).
    std::atomic_store(&engine_, std::make_shared<EjfatPacketizeEngine>());
    return {};
}


ersap::EngineData EjfatPacketizeService::execute(ersap::EngineData& input) {
    auto output = ersap::EngineData{};

    time_t end = time(nullptr);

    // Pull out needed config items
    //static int sendBuffer(char *buffer, uint32_t bufLen
    int *eventNum = data_cast<int*>(input);
    uint64_t tick = *eventNum;

    any & a = input.data();
    int *eventNumber = any_cast<int *>(a);

    input.mime_type();

    //input.getMeta()->datatype();


    if (end - start >= 10) {
        // This always loads the shared_pointer into a new shared_ptr
        std::atomic_load(&engine_)->process();
        start = end;
    }
    // Set and return output data
    //    output.set_data(IMAGE_TYPE, img);
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
    return { ersap::type::JSON, ersap::type::BYTES };
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
    return "Vardan Gyurjyan";
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
