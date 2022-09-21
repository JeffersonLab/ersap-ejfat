//
// Copyright 2022, Jefferson Science Associates, LLC.
// Subject to the terms in the LICENSE file found in the top-level directory.
//
// EPSCI Group
// Thomas Jefferson National Accelerator Facility
// 12000, Jefferson Ave, Newport News, VA 23606
// (757)-269-7100
//
// Created by Vardan Gyurjyan on 9/16/22.
//
/**
 * <p>
 * @file Send a data buffer to an ejfat Load balancer (FPGA-based)
 * </p>
 */
#include "packetizeAndSend.h"

static void packetSend(char *buf) {
    int mtu = 9000;
    struct sockaddr_in serverAddr;

    // 20 bytes = normal IPv4 packet header (60 is max), 8 bytes = max UDP packet header
    // https://stackoverflow.com/questions/42609561/udp-maximum-packet-size
    int maxUdpPayload = mtu - 20 - 8 - HEADER_BYTES;

    // Create UDP socket
    if ((clientSocket = socket(PF_INET, SOCK_DGRAM, 0)) < 0) {
        perror("creating IPv4 client socket");
        return -1;
    }

    socklen_t size = sizeof(int);
    int sendBufBytes = 0;
}
