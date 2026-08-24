package com.agri.costtracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.agri.costtracker.data.local.FakeAgriDao
import com.agri.costtracker.data.repository.AgriRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class AgriViewModelFactoryTest {

    private lateinit var fakeDao: FakeAgriDao
    private lateinit var repository: AgriRepository
    private lateinit var factory: AgriViewModelFactory

    @Before
    fun setUp() {
        fakeDao = FakeAgriDao()
        repository = AgriRepository(fakeDao)
        factory = AgriViewModelFactory(repository)
    }

    @Test
    fun create_agriViewModel_returnsInstance() {
        val viewModel = factory.create(AgriViewModel::class.java)
        assertNotNull(viewModel)
        assertEquals(AgriViewModel::class.java, viewModel.javaClass)
    }

    private class UnsupportedViewModel : ViewModel()

    @Test(expected = IllegalArgumentException::class)
    fun create_unknownViewModel_throwsIllegalArgumentException() {
        factory.create(UnsupportedViewModel::class.java)
    }
}
