package ru.nekit.android.data

import android.content.SharedPreferences
import ru.nekit.android.domain.repository.ICounter

class Counter(sharedPreferences: SharedPreferences,
              private val name: String) : ICounter {

    private val intStore = StringKeyIntValueStore(sharedPreferences)

    override var startValue: Int = 0

    override var value: Int
        get() = intStore.get(getValueName(name), startValue)
        set(value) {
            intStore.put(getValueName(name), value)
        }

    override fun reset() {
        setCounterValue(startValue)
    }

    private fun setCounterValue(value: Int) {
        this.value = value
    }

    override fun zeroWasReached(): Boolean {
        return value == 0
    }

    override fun countDown() {
        count(-1)
    }

    override fun countUp() {
        count(1)
    }

    private fun count(value: Int) {
        var counterValue = this.value
        counterValue += value
        counterValue = Math.max(0, counterValue)
        this.value = counterValue
    }

    companion object {

        private fun getValueName(name: String): String {
            return String.format("countdown.value.%s", name)
        }


    }

}