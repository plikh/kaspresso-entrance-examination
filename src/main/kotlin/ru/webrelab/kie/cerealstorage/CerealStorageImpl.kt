package ru.webrelab.kie.cerealstorage



class CerealStorageImpl(
    override val containerCapacity: Float,
    override val storageCapacity: Float
) : CerealStorage {
    init {
        require(containerCapacity >= 0) {
            "Capacity of container value can not be negative"
        }
        require(storageCapacity >= containerCapacity) {
            "Capacity of container value must be less or equal storage capacity"
        }
    }

    val mainStorage = mutableMapOf<Cereal, Float>()
    private val maxContainersCount = (storageCapacity / containerCapacity).toInt()
    private var currentContainersCount = 0


    override fun addCereal(cereal: Cereal, amount: Float): Float {
        require(amount >= 0) { "Amount value can not be negative" }
        if (cereal !in mainStorage.keys) {
            if ((currentContainersCount + 1) > maxContainersCount) throw IllegalStateException(
                "Storage is full can not add new container"
            )
            mainStorage[cereal] = 0f
            currentContainersCount += 1
        }
        val currentAmount = mainStorage.getOrDefault(cereal, 0f)
        if ((currentAmount + amount) > containerCapacity) {
            mainStorage[cereal] = containerCapacity
            return (amount - (containerCapacity - currentAmount))
        }
        mainStorage[cereal] = currentAmount + amount
        return 0f
    }

    override fun getCereal(cereal: Cereal, amount: Float): Float {
        require(amount > 0) { "Amount value can not be negative" }
        if (cereal !in mainStorage.keys) {
            throw NoCerealException("Specified cereal type: $cereal is absent in storage")
        }
        val currentContainerState = mainStorage.getOrDefault(cereal, 0f)
        if (amount > currentContainerState) {
            mainStorage[cereal] = 0f
            return amount - currentContainerState
        }
        mainStorage[cereal] = currentContainerState - amount
        return amount
    }


    override fun removeContainer(cereal: Cereal): Boolean {
        if ((cereal !in mainStorage.keys) || mainStorage[cereal] != 0f) {
            return false
        }
        mainStorage.remove(cereal)
        currentContainersCount -= 1
        return true

    }

    override fun getAmount(cereal: Cereal): Float {
        return mainStorage.getOrDefault(cereal, 0f)
    }

    override fun getSpace(cereal: Cereal): Float {
        val currentState = mainStorage.getOrDefault(cereal, 0f)
        if (currentState == 0f) {
            if ((currentContainersCount + 1) > maxContainersCount) throw IllegalStateException("No space to store $cereal")
            return containerCapacity
        }
        return containerCapacity - currentState
    }


    override fun toString(): String {
        val cereals = mainStorage.entries.joinToString(", ") {
            "${it.key} = ${it.value}"
        }
        return """
            Container capacity: $containerCapacity;
            Storage Capacity: $storageCapacity;
            Cereals: $cereals
        """.trimIndent()
    }

}
