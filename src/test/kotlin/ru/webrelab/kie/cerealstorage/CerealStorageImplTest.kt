package ru.webrelab.kie.cerealstorage

import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CerealStorageImplTest {

    private lateinit var storage: CerealStorageImpl

    @BeforeEach
    fun setup() {
        storage = CerealStorageImpl(containerCapacity = 25f, storageCapacity = 70f)
    }

    @Test
    fun `exception when pass negative containerCapacity value`() {
        val exception = assertThrows<IllegalArgumentException> {
            CerealStorageImpl(containerCapacity = -10f, storageCapacity = 9f)
        }
        assertEquals("Capacity of container value can not be negative", exception.message)

    }


    @Test
    fun `exception when pass negative containerCapacity value two part`() {
        val exception = assertThrows(IllegalArgumentException::class.java, {
            CerealStorageImpl(containerCapacity = -10f, storageCapacity = 9f)
        })
        assertEquals("Capacity of container value can not be negative", exception.message)
    }


    @Test
    fun `exception when pass containerCapacity more than storageCapacity`() {
        val exception = assertThrows<IllegalArgumentException> {
            CerealStorageImpl(containerCapacity = 10f, storageCapacity = 9f)
        }
        assertEquals(
            "Capacity of container value must be less or equal storage capacity",
            exception.message
        )
    }

    @Test
    fun `exception when pass negative amount value to addCereal`() {
        val exception = assertThrows<IllegalArgumentException> {
            storage.addCereal(Cereal.BULGUR, -19f)
        }
        assertEquals("Amount value can not be negative", exception.message)
    }


    @Test
    fun `exception when can not add additional container for new kind of cereal`() {
        storage.addCereal(Cereal.PEAS, 25f)
        storage.addCereal(Cereal.BUCKWHEAT, 19f)
        val exception = assertThrows<IllegalStateException> { storage.addCereal(Cereal.MILLET, 25f) }
        assertEquals("Storage is full can not add new container", exception.message)
    }

    @Test
    fun `get difference of cereal when there is no particular container`() {
        val result = storage.addCereal(Cereal.BUCKWHEAT, 49f)
        assertTrue(Cereal.BUCKWHEAT in storage.getStorage().keys)
        assertEquals(24f, result)
    }

    @Test
    fun `get difference of cereal when container is full`() {
        storage.addCereal(Cereal.BUCKWHEAT, 20f)
        val result = storage.addCereal(Cereal.BUCKWHEAT, 35f)
        assertEquals(30f, result)
    }

    @Test
    fun `get zero value and check cereal added to container`() {
        val result = storage.addCereal(Cereal.BUCKWHEAT, 0.0234f)
        assertEquals(0f, result, "Wrong value: $result from addCereal method")
        val cerealAmount = storage.getStorage()[Cereal.BUCKWHEAT]
        assertEquals(0.0234f, cerealAmount, "Wrong value: $cerealAmount from ${Cereal.BUCKWHEAT} container")
    }

    @Test
    fun `exception when storage does not contain passed cereal type`() {
        val exception = assertThrows<NoCerealException> { storage.getCereal(Cereal.MILLET, 0.098f) }
        assertEquals("Specified cereal type: ${Cereal.MILLET} is absent in storage", exception.message)
    }

    @Test
    fun `get correct cereal amount`() {
        storage.addCereal(Cereal.BUCKWHEAT, 13.901f)
        val result = storage.getCereal(Cereal.BUCKWHEAT, 12f)
        assertEquals(12f, result, 0.01f)
    }

    @Test
    fun `get correct difference cereal amount`() {
        storage.addCereal(Cereal.BUCKWHEAT, 19f)
        val result = storage.getCereal(Cereal.BUCKWHEAT, 21f)
        assertEquals(19f, result)
    }

    @Test
    fun `get false when trying remove container which does not exist in storage`() {
        assertFalse(storage.removeContainer(Cereal.PEAS))
    }

    @Test
    fun `get false when trying remove container which has some amount of cereal`() {
        storage.addCereal(Cereal.PEAS, 0.043f)
        assertFalse(storage.removeContainer(Cereal.PEAS))
    }

    @Test
    fun `get true when remove empty container`() {
        storage.addCereal(Cereal.PEAS, 0f)
        assertTrue(storage.removeContainer(Cereal.PEAS))
        assertFalse(storage.getStorage().keys.contains(Cereal.PEAS))
    }

    @Test
    fun `get correct amount`() {
        storage.addCereal(Cereal.RICE, 12.0989f)
        assertEquals(storage.getAmount(Cereal.RICE), 12.0989f, 0.01f)
    }

    @Test
    fun `get correct amount when we add empty container`() {
        storage.addCereal(Cereal.RICE, 0f)
        assertEquals(storage.getAmount(Cereal.RICE), 0f, 0.01f)
    }

    @Test
    fun `get correct free container space`() {
        storage.addCereal(Cereal.RICE, 24f)
        assertEquals(storage.getSpace(Cereal.RICE), 1f, 0.01f)
    }

    @Test
    fun `get max container space`() {
        assertEquals(storage.getSpace(Cereal.RICE), 25f, 0.01f)
    }

    @Test
    fun `exception when checking free space and can not add new container`() {
        storage.addCereal(Cereal.BUCKWHEAT, 20f)
        storage.addCereal(Cereal.RICE, 20f)
        val exception = assertThrows<IllegalStateException> { storage.getSpace(Cereal.MILLET) }
        assertEquals("No space to store ${Cereal.MILLET}", exception.message)
    }
}