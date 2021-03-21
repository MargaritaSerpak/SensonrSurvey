package com.example.sensonrsurvey

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.activity_main.*

// Платформа датчиков Андроид позволяет регестрировать и реагировать на изменения данных датчиков
class MainActivity : AppCompatActivity(), SensorEventListener {

    /** Диспетчер датчиков - системная служба, которая позволяет получить доступ к датч. устройства */
    private lateinit var mSensorManager: SensorManager

    // Индивидуальные датчики освещенности и  приближения
    private var mSensorProximity: Sensor? = null
    private var mSensorLight: Sensor? = null

    // TW отображающие текущие сенсорные значения
    private lateinit var mTextSensorProximity: TextView
    private lateinit var mTextSensorLight: TextView

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // получ. экземпляр дисп.датч. и  присв. перемен.
        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        // получ. tw и назнач. их соответств. переменным
        mTextSensorProximity = findViewById(R.id.label_proximity)
        mTextSensorLight = findViewById(R.id.label_light)

        //Получаем  экземпляры датчиков освещ. и приближ. по умолчанию
        mSensorProximity = this.mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
        mSensorLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

        // Проверка наличия датчиков
        val sensorError: String = resources.getString(R.string.error_no_sensor)
        if (mSensorLight == null) {
            mTextSensorLight.text = sensorError
        }

        if (mSensorProximity == null) {
            mTextSensorProximity.text = sensorError
        }


        val rangeSensor = 0.00
        if (mSensorLight != null) {
            when(rangeSensor) {
                in 0.00..2.00 -> view.setBackgroundColor(R.color.light_red)
                in 3.00..4.00 -> view.setBackgroundColor(R.color.yellow)
            }
        }


        /* // получ. весь список датчиков и присваиваем переменн. типа List
         */
        /** @Sensor Класс представляет собой отдельный датчик и определяет const для
         * доступных типов датчиков.
         * @Sensor.TYPE_ALL Константа указывает все доступные датчики */
        /*
             val sensorList: List<Sensor> = mSensorManager.getSensorList(Sensor.TYPE_ALL)

            // получ. офиц. имена датчиков и уст. их в строку, разделяя новой строкой
            var sensorText: StringBuilder = StringBuilder()
            for (currentSensor: Sensor in  sensorList) { // append - добавить
                sensorText.append(currentSensor.name).append(getProperty("line.separator"))
            }

            // получ. ссылку на tw и обновляем текст представления строкой, содерж. список датч.
            val sensorTextView: TextView = findViewById(R.id.sensor_list)
            sensorTextView.text = sensorText*/

    }


    /** Регестрировать новых слушателей лучше всего в метотдах onStart() и  onStop()
     * так как прослушивание поступающих данных с датчиков расходует энергию устройства
     * и расходует заряд батареи. */
    override fun onStart() {
        super.onStart()

        if (mSensorProximity != null) {
            mSensorManager.registerListener(this,
                    mSensorProximity, SensorManager.SENSOR_DELAY_NORMAL)
        }

        if (mSensorLight != null) {
            mSensorManager.registerListener(this,
                    mSensorLight, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onStop() {
        super.onStop()
        /** @unregisterListener() отменяет регистрацию всех зарегестрированных слушателей.
         * Отмена регистрации приемников позволяет экономить питание, когда приложение
         * не отображается.
         */
        mSensorManager.unregisterListener(this)
    }

    /** SensorEventListener интерфейс, включает в себя два метода обратного вызоыва, которые
     * позволяют приложению обрабатывать события датчиков.
     *
     * @onSensorChanged() вызывается, когда доступны новые данные датчика
     * @onAccuracyChanged() вызывается, если точность датчика изменяется*/
    override fun onSensorChanged(event: SensorEvent?) {
        /** @onSensorChanged вызывается вместе с объектом Sensor Event,
         * который в свою очередь включает в себя важные свойства: сообщения о новых данных
         * и значения этих данных. */
        val sensorType: Int = event?.sensor!!.type
        val currentValue: Float = event.values[0] // событие сохраняет новые данные в массиве

        when (sensorType) {
            Sensor.TYPE_LIGHT -> {
                mTextSensorLight.text = resources.getString(R.string.light_sensor, currentValue)
            }
            Sensor.TYPE_PROXIMITY -> {
                mTextSensorProximity.text = resources.getString(R.string.proximity_sensor,
                        currentValue)
            }
            else -> {
                //do nothing
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }
}