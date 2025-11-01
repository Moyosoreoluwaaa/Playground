package com.playground.oakk.uistates

import androidx.compose.runtime.Immutable
import com.playground.oakk.uistates.mockBanks

data class OakkBank(
    val id: String,
    val name: String,
    val logoUrl: String
)

@Immutable
data class OakkLinkBankAccountUiState(
    val title: String = "Link an existing bank account",
    val subtitle: String = "I don't know much about the benefits or potential risks of investing.",
    val searchQuery: String = "",
    val availableBanks: List<OakkBank> = mockBanks,
    val filteredBanks: List<OakkBank> = mockBanks,
    val isLoading: Boolean = false,
    val error: String? = null
)
private val SampleBanks = listOf(
    OakkBank(id = "1", name = "Chase Bank", logoUrl = "https://example.com/chase.png"),
    OakkBank(id = "2", name = "Bank of America", logoUrl = "https://example.com/bofa.png"),
    OakkBank(id = "3", name = "Wells Fargo", logoUrl = "https://example.com/wells.png"),
    OakkBank(id = "4", name = "Citibank", logoUrl = "https://example.com/citi.png"),
    OakkBank(id = "5", name = "US Bank", logoUrl = "https://example.com/usbank.png"),
    OakkBank(id = "6", name = "PNC Bank", logoUrl = "https://example.com/pnc.png"),
    OakkBank(id = "7", name = "TD Bank", logoUrl = "https://example.com/td.png"),
    OakkBank(id = "8", name = "Capital One", logoUrl = "https://example.com/capone.png"),
    OakkBank(id = "9", name = "Truist Bank", logoUrl = "https://example.com/truist.png"),
    OakkBank(id = "10", name = "Goldman Sachs", logoUrl = "https://example.com/gs.png"),
    OakkBank(id = "11", name = "Ally Bank", logoUrl = "https://example.com/ally.png"),
    OakkBank(id = "12", name = "Discover Bank", logoUrl = "https://example.com/discover.png"),
    OakkBank(id = "13", name = "First Horizon", logoUrl = "https://example.com/fh.png"),
    OakkBank(id = "14", name = "KeyBank", logoUrl = "https://example.com/key.png"),
    OakkBank(id = "15", name = "Santander Bank", logoUrl = "https://example.com/santander.png"),
    OakkBank(id = "16", name = "M&T Bank", logoUrl = "https://example.com/mt.png"),
    OakkBank(id = "17", name = "Huntington Bank", logoUrl = "https://example.com/huntington.png"),
    OakkBank(id = "18", name = "BMO Harris Bank", logoUrl = "https://example.com/bmo.png"),
    OakkBank(id = "19", name = "Regions Bank", logoUrl = "https://example.com/regions.png"),
    OakkBank(id = "20", name = "Navy Federal CU", logoUrl = "https://example.com/nfcu.png")
)

private val mockBanks = listOf(
    OakkBank("1", "Chase Bank", "https://example.com/chase.png"),
    OakkBank("2", "Citi Bank", "https://example.com/citi.png"),
    OakkBank("3", "First United Bank", "https://example.com/firstunited.png"),
    OakkBank("4", "Ally Bank", "https://example.com/ally.png"),
    OakkBank("5", "American Express Bank", "https://example.com/amex.png"),
    OakkBank("6", "Bank of America", "https://example.com/bofa.png"),
    OakkBank("7", "Benepass Bank", "https://example.com/benepass.png"),
    // --- Added 3 new banks below ---
    OakkBank("8", "Wells Fargo", "https://example.com/wellsfargo.png"),
    OakkBank("9", "Capital One", "https://example.com/capitalone.png"),
    OakkBank("10", "TD Bank", "https://example.com/tdbank.png")
)