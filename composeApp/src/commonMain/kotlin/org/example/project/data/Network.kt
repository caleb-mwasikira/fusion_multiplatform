package org.example.project.data

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.Collections

object Network {
    fun getLocalIPAddress(): String? {
        val interfaces = NetworkInterface.getNetworkInterfaces()

        for (iface in interfaces) {
            if (iface.isUp && !iface.isLoopback) {
                val addresses = Collections.list(iface.inetAddresses)
                for (address in addresses) {
                    val ip = address.hostAddress
                    if (ip.contains('.') && !ip.startsWith("127")) {
                        return ip
                    }
                }
            }
        }
        return null
    }

    suspend fun getNetworkDevices(): List<String> = withContext(Dispatchers.IO) {
        println("Running network discovery...")
        val localIP = getLocalIPAddress() ?: run {
            println("Error acquiring local IP address")
            return@withContext emptyList()
        }
        val subnet = localIP.substringBeforeLast('.') // Get network address 192.168.0
        val semaphore = Semaphore(50)
        val jobs = mutableListOf<Deferred<String?>>()

        // Starting with 2 so that we do not fetch the network address 192.168.0.1
        for (i in 2..254) {
            val job: Deferred<String?> = async {
                semaphore.withPermit {
                    val ip = "$subnet.$i"
                    if (ip == localIP) return@withPermit null

                    try {
                        val address = InetAddress.getByName(ip)
                        if (address.isReachable(300)) {
                            println("Device $ip online")
                            return@withPermit ip
                        }
                        return@withPermit null
                    } catch (e: Exception) {
                        println("Error pinging device $ip; ${e.message}")
                        return@withPermit null
                    }
                }
            }
            jobs += job
        }

        val networkDevices = jobs.awaitAll().filterNotNull()
        return@withContext networkDevices
    }
}