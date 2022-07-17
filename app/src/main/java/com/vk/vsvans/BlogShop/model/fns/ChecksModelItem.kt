package com.vk.vsvans.BlogShop.model.fns

data class ChecksModelItem(
    val claims: List<Any>,
    val createdAt: String,
    val ticket: Ticket,
    val timeLastAccess: String
)