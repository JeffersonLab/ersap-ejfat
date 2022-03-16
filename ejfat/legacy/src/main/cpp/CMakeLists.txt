
set(LEGACY_SRC
        ejfat_packetize_engine
        ejfat_assemble_engine
        ejfat_assemble_et_engine
)

add_library(ersap_ejfat SHARED ${LEGACY_SRC})
target_link_libraries(ersap_ejfat PUBLIC ${ZeroMQ_LIBRARIES})
target_include_directories(ersap_ejfat PUBLIC ${ZeroMQ_INCLUDE_DIRS}
  $<BUILD_INTERFACE:${CMAKE_CURRENT_SOURCE_DIR}>
)

add_library(ersap_ejfat SHARED IMPORTED)
set_target_properties(ersap_ejfat PROPERTIES
  IMPORTED_LOCATION "$ENV{CODA}/Linux-x86_64/lib/libet.so"
  IMPORTED_LOCATION "$ENV{CODA}/Linux-x86_64/lib/libet_remote.so"
  IMPORTED_LOCATION "$ENV{CODA}/Linux-x86_64/lib/libet_jni.so"
  INTERFACE_INCLUDE_DIRECTORIES "$ENV{CODA}/common/include"
)


install(TARGETS ersap_ejfat DESTINATION ${CMAKE_INSTALL_PLUGINDIR}/ejfat/lib)
