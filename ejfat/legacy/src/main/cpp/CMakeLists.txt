
set(LEGACY_SRC
        ejfat_packetize_engine
        ejfat_assemble_engine
        ejfat_assemble_et_engine
)

add_library(ersap_ejfat SHARED ${LEGACY_SRC})
target_link_libraries(ersap_ejfat PUBLIC ${ZeroMQ_LIBRARIES})
target_link_libraries(ersap_ejfat PUBLIC "$ENV{CODA}/Linux-x86_64/lib/libet.so")
target_link_libraries(ersap_ejfat PUBLIC "$ENV{CODA}/Linux-x86_64/lib/libet_remote.so")
target_link_libraries(ersap_ejfat PUBLIC "$ENV{CODA}/Linux-x86_64/lib/libet_jni.so")

target_include_directories(ersap_ejfat PUBLIC ${ZeroMQ_INCLUDE_DIRS}
  $<BUILD_INTERFACE:${CMAKE_CURRENT_SOURCE_DIR}>
)

target_include_directories(ersap_ejfat PUBLIC "$ENV{CODA}/common/include"
  $<BUILD_INTERFACE:${CMAKE_CURRENT_SOURCE_DIR}>
)

install(TARGETS ersap_ejfat DESTINATION ${CMAKE_INSTALL_PLUGINDIR}/ejfat/lib)
