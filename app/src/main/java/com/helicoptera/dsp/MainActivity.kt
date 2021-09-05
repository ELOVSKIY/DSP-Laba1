package com.helicoptera.dsp

import android.animation.Animator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import kotlin.math.PI
import kotlin.math.sin


class MainActivity : AppCompatActivity() {

    private var a1 = 0.0
    private var a2 = 0.0
    private var a3 = 0.0
    private var a4 = 0.0
    private var a5 = 0.0

    private var f1 = 0.0
    private var f2 = 0.0
    private var f3 = 0.0
    private var f4 = 0.0
    private var f5 = 0.0

    private var lambda1 = 0.0
    private var lambda2 = 0.0
    private var lambda3 = 0.0
    private var lambda4 = 0.0
    private var lambda5 = 0.0

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        resetValues()
        findViewById<Button>(R.id.reset_button).setOnClickListener {
            resetValues()
        }
        findViewById<Button>(R.id.calculate_button).setOnClickListener {
            initializeValues()
            calculate()
        }

        val animatedSwitch = findViewById<SwitchCompat>(R.id.animated)
        animatedSwitch.setOnClickListener {
            animate(animatedSwitch.isChecked)
        }
    }

    private fun animate(isAnimated: Boolean) {
        if (isAnimated) {
            val runnable = object : Runnable {
                override fun run() {
                    indexValue()
                    calculate ()
                    handler.postDelayed(this, ANIMATE_DELAY)
                }
            }
            runnable.run()
        } else {
            handler.removeCallbacksAndMessages(null)
        }
    }

    private fun indexValue() {
        a1 *= ANIMATE_FACTOR
        a2 *= ANIMATE_FACTOR
        a3 *= ANIMATE_FACTOR
        a4 *= ANIMATE_FACTOR
        a5 *= ANIMATE_FACTOR

        f1 *= ANIMATE_FACTOR
        f2 *= ANIMATE_FACTOR
        f3 *= ANIMATE_FACTOR
        f4 *= ANIMATE_FACTOR
        f5 *= ANIMATE_FACTOR

        lambda1 *= ANIMATE_FACTOR
        lambda2 *= ANIMATE_FACTOR
        lambda3 *= ANIMATE_FACTOR
        lambda4 *= ANIMATE_FACTOR
        lambda5 *= ANIMATE_FACTOR
    }

    private fun initializeValues() {
        a1 = findViewById<EditText>(R.id.a_1).text.toString().toDouble()
        a2 = findViewById<EditText>(R.id.a_2).text.toString().toDouble()
        a3 = findViewById<EditText>(R.id.a_3).text.toString().toDouble()
        a4 = findViewById<EditText>(R.id.a_4).text.toString().toDouble()
        a5 = findViewById<EditText>(R.id.a_5).text.toString().toDouble()

        f1 = findViewById<EditText>(R.id.f_1).text.toString().toDouble()
        f2 = findViewById<EditText>(R.id.f_2).text.toString().toDouble()
        f3 = findViewById<EditText>(R.id.f_3).text.toString().toDouble()
        f4 = findViewById<EditText>(R.id.f_4).text.toString().toDouble()
        f5 = findViewById<EditText>(R.id.f_5).text.toString().toDouble()

        val lambda1Divider = PI / findViewById<EditText>(R.id.lambda_1).text.toString().toDouble()
        val lambda2Divider = PI / findViewById<EditText>(R.id.lambda_2).text.toString().toDouble()
        val lambda3Divider = PI / findViewById<EditText>(R.id.lambda_3).text.toString().toDouble()
        val lambda4Divider = PI / findViewById<EditText>(R.id.lambda_4).text.toString().toDouble()
        val lambda5Divider = PI / findViewById<EditText>(R.id.lambda_5).text.toString().toDouble()
        lambda1 = if (lambda1Divider != 0.0) PI / lambda1Divider else 0.0
        lambda2 = if (lambda2Divider != 0.0) PI / lambda1Divider else 0.0
        lambda3 = if (lambda3Divider != 0.0) PI / lambda1Divider else 0.0
        lambda4 = if (lambda4Divider != 0.0) PI / lambda1Divider else 0.0
        lambda5 = if (lambda5Divider != 0.0) PI / lambda1Divider else 0.0
    }

    private fun calculate() {
        val isPolyharmonic = findViewById<SwitchCompat>(R.id.poly_signal_active).isChecked
        val graphicsValues = if (isPolyharmonic) {
            val aValues = mutableListOf(a1, a2, a3, a4, a5)
            val fValues = mutableListOf(f1, f2, f3, f4, f5)
            val lambdaValues = mutableListOf(lambda1, lambda2, lambda3, lambda4, lambda5)
            val values = polyharmonicFunction(aValues, fValues, lambdaValues)
            mutableListOf(values)
        } else {
            val values1 = harmonicFunction(a1, f1, lambda1)
            val values2 = harmonicFunction(a2, f2, lambda2)
            val values3 = harmonicFunction(a3, f3, lambda3)
            val values4 = harmonicFunction(a4, f4, lambda4)
            val values5 = harmonicFunction(a5, f5, lambda5)
            mutableListOf(values1, values2, values3, values4, values5)
        }
        drawGraphic(graphicsValues)
    }

    private fun drawGraphic(graphicsValues: List<List<Double>>) {
        val chart = findViewById<LineChart>(R.id.chart)
        val dataSets = mutableListOf<ILineDataSet>()
        for (i in graphicsValues.indices) {
            val values = graphicsValues[i]

            val entries = mutableListOf<Entry>()
            for (j in values.indices) {
                val value = values[j]
                val entry = Entry(j.toFloat(), value.toFloat())
                entries.add(entry)
            }
            val graphicTitle = if (graphicsValues.size > 1) i.toString() else "Graphic"
            val lineDataSet = LineDataSet(entries, graphicTitle)
            val colors = ColorTemplate.MATERIAL_COLORS.toList()
            lineDataSet.setCircleColor(colors[i % colors.size])
            dataSets.add(lineDataSet)
        }
        val data = LineData(dataSets)
        chart.data = data;
        chart.invalidate();
    }

    private fun harmonicFunction(a: Double, f: Double, lambda: Double): List<Double> {
        val values = mutableListOf<Double>()
        for (n in 0 until N) {
            val value = a * sin((2 * PI * f * n) / N + lambda)
            values.add(value)
        }

        return values
    }

    private fun polyharmonicFunction(
        a: List<Double>,
        f: List<Double>,
        lambda: List<Double>
    ): List<Double> {
        val values = mutableListOf<Double>()
        for (n in 0 until N) {
            var value = 0.0
            for (j in 0 until HARMONIC_COUNT) {
                value += a[j] * sin((2 * PI * f[j] * n) / N + lambda[j])
            }
            values.add(value)
        }

        return values
    }

    private fun resetValues() {
        findViewById<EditText>(R.id.a_1).setText(1.toString())
        findViewById<EditText>(R.id.a_2).setText(2.toString())
        findViewById<EditText>(R.id.a_3).setText(3.toString())
        findViewById<EditText>(R.id.a_4).setText(4.toString())
        findViewById<EditText>(R.id.a_5).setText(5.toString())
        findViewById<EditText>(R.id.f_1).setText(1.toString())
        findViewById<EditText>(R.id.f_2).setText(2.toString())
        findViewById<EditText>(R.id.f_3).setText(3.toString())
        findViewById<EditText>(R.id.f_4).setText(4.toString())
        findViewById<EditText>(R.id.f_5).setText(5.toString())
        findViewById<EditText>(R.id.lambda_1).setText(1.toString())
        findViewById<EditText>(R.id.lambda_2).setText(2.toString())
        findViewById<EditText>(R.id.lambda_3).setText(3.toString())
        findViewById<EditText>(R.id.lambda_4).setText(4.toString())
        findViewById<EditText>(R.id.lambda_5).setText(5.toString())

    }

    companion object {
        private const val N = 2048
        private const val HARMONIC_COUNT = 3
        private const val ANIMATE_DELAY = 100L
        private const val ANIMATE_FACTOR = 1.005
    }
}