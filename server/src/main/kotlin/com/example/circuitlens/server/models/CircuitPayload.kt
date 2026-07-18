package com.example.circuitlens.server.models

import kotlinx.serialization.Serializable

@Serializable
data class Circuit(
    val id: String,
    val name: String,
    val components: List<Component>
)

@Serializable
data class Component(
    val id: String,
    val type: String, // e.g. "RESISTOR", "CAPACITOR", "INDUCTOR", "DC_VOLTAGE", "DC_CURRENT"
    val value: Double,
    val pins: List<Pin>
)

@Serializable
data class Pin(
    val name: String, // e.g. "pos", "neg", "p1", "p2"
    val nodeId: String // e.g. "0" (ground), "1", "2"
)
