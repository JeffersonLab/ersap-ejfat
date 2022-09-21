# Create one shared library per engine
set(SOURCE_ENGINES
        packetizeAndSend
        )

foreach(engine ${DEMO_SERVICES})
    add_library(${engine} SHARED ${engine}.cpp)
    target_link_libraries(${engine} ersap_ejfat Ersap::ersap)
    if (CMAKE_CXX_COMPILER_ID MATCHES Clang)
        target_compile_options(${engine} PRIVATE -Wno-return-type-c-linkage)
    endif()
endforeach()

install(TARGETS ${SOURCE_ENGINES} DESTINATION ${CMAKE_INSTALL_PLUGINDIR}/ejfat/services)
