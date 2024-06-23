import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import campa.aracelyl.myfeelings.CustomBarDrawable
import campa.aracelyl.myfeelings.CustomCircleDrawable
import campa.aracelyl.myfeelings.Emociones
import campa.aracelyl.myfeelings.JSONFile
import campa.aracelyl.myfeelings.R
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    var jsonFile: JSONFile? = null
    var veryHappy = 0.0F
    var happy = 0.0F
    var neutral = 0.0F
    var sad = 0.0F
    var verySad = 0.0F
    var data: Boolean = false
    var lista = ArrayList<Emociones>()

    private lateinit var icon: ImageView
    private lateinit var graph: View
    private lateinit var graphVeryHappy: View
    private lateinit var graphHappy: View
    private lateinit var graphNeutral: View
    private lateinit var graphSad: View
    private lateinit var graphVerySad: View
    private lateinit var guardarButton: Button
    private lateinit var veryHappyButton: ImageButton
    private lateinit var happyButton: ImageButton
    private lateinit var neutralButton: ImageButton
    private lateinit var sadButton: ImageButton
    private lateinit var verySadButton: ImageButton

    private fun fetchingData() {
        try {
            val json: String = jsonFile?.getData(this) ?: ""
            if (json.isNotEmpty()) {
                this.data = true
                val jsonArray = JSONArray(json)
                this.lista = parseJson(jsonArray)

                for (i in lista) {
                    when (i.nombre) {
                        "Muy feliz" -> veryHappy = i.total
                        "Feliz" -> happy = i.total
                        "Neutral" -> neutral = i.total
                        "Triste" -> sad = i.total
                        "Muy triste" -> verySad = i.total
                    }
                }
            } else {
                this.data = false
            }
        } catch (exception: JSONException) {
            exception.printStackTrace()
        }
    }

    private fun iconoMayoria() {
        when {
            happy > veryHappy && happy > neutral && happy > sad && happy > verySad -> {
                icon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_happy))
            }
            veryHappy > happy && veryHappy > neutral && veryHappy > sad && veryHappy > verySad -> {
                icon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_veryhappy))
            }
            neutral > veryHappy && neutral > happy && neutral > sad && neutral > verySad -> {
                icon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_neutral))
            }
            sad > happy && sad > neutral && sad > veryHappy && sad > verySad -> {
                icon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_sad))
            }
            verySad > happy && verySad > neutral && verySad > sad && verySad > veryHappy -> {
                icon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_verysad))
            }
        }
    }

    private fun actualizarGrafica() {
        val total = veryHappy + happy + neutral + verySad + sad

        val pVH: Float = (veryHappy * 100 / total).toFloat()
        val pH: Float = (happy * 100 / total).toFloat()
        val pN: Float = (neutral * 100 / total).toFloat()
        val pS: Float = (sad * 100 / total).toFloat()
        val pVS: Float = (verySad * 100 / total).toFloat()

        Log.d("porcentajes", "very happy $pVH")
        Log.d("porcentajes", "happy $pH")
        Log.d("porcentajes", "neutral $pN")
        Log.d("porcentajes", "sad $pS")
        Log.d("porcentajes", "very sad $pVS")

        lista.clear()
        lista.add(Emociones("Muy feliz", pVH, R.color.mustard, veryHappy))
        lista.add(Emociones("Feliz", pH, R.color.orange, happy))
        lista.add(Emociones("Neutral", pN, R.color.greenie, neutral))
        lista.add(Emociones("Triste", pS, R.color.blue, sad))
        lista.add(Emociones("Muy triste", pVS, R.color.deepBlue, verySad))

        val fondo = CustomCircleDrawable(this, lista)
        graphVeryHappy.background = CustomBarDrawable(this, Emociones("Muy feliz", pVH, R.color.mustard, veryHappy))
        graphHappy.background = CustomBarDrawable(this, Emociones("Feliz", pH, R.color.orange, happy))
        graphNeutral.background = CustomBarDrawable(this, Emociones("Neutral", pN, R.color.greenie, neutral))
        graphSad.background = CustomBarDrawable(this, Emociones("Triste", pS, R.color.blue, sad))
        graphVerySad.background = CustomBarDrawable(this, Emociones("Muy triste", pVS, R.color.deepBlue, verySad))

        graph.background = fondo
    }

    private fun parseJson(jsonArray: JSONArray): ArrayList<Emociones> {
        val lista = ArrayList<Emociones>()
        for (i in 0 until jsonArray.length()) {
            try {
                val nombre = jsonArray.getJSONObject(i).getString("nombre")
                val porcentaje = jsonArray.getJSONObject(i).getDouble("porcentaje").toFloat()
                val color = jsonArray.getJSONObject(i).getInt("color")
                val total = jsonArray.getJSONObject(i).getDouble("total").toFloat()
                val emocion = Emociones(nombre, porcentaje, color, total)
                lista.add(emocion)
            } catch (exception: JSONException) {
                exception.printStackTrace()
            }
        }
        return lista
    }

    private fun guardar() {
        val jsonArray = JSONArray()
        var o = 0
        for (i in lista) {
            Log.d("objetos", i.toString())
            val j = JSONObject()
            j.put("nombre", i.nombre)
            j.put("porcentaje", i.porcentaje)
            j.put("color", i.color)
            j.put("total", i.total)

            jsonArray.put(o, j)
            o++
        }

        jsonFile?.saveData(this, jsonArray.toString())

        Toast.makeText(this, "Datos guardados", Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        jsonFile = JSONFile()

        icon = findViewById(R.id.icon)
        graph = findViewById(R.id.graph)
        graphVeryHappy = findViewById(R.id.graphVeryHappy)
        graphHappy = findViewById(R.id.graphHappy)
        graphNeutral = findViewById(R.id.graphNeutral)
        graphSad = findViewById(R.id.graphSad)
        graphVerySad = findViewById(R.id.graphVerySad)
        guardarButton = findViewById(R.id.guardarButton)
        veryHappyButton = findViewById(R.id.veryHappyButton)
        happyButton = findViewById(R.id.happyButton)
        neutralButton = findViewById(R.id.neutralButton)
        sadButton = findViewById(R.id.sadButton)
        verySadButton = findViewById(R.id.verySadButton)

        fetchingData()
        if (data) {
            val emociones = ArrayList<Emociones>()
            val fondo = CustomCircleDrawable(this, emociones)
            graph.background = fondo
            graphVeryHappy.background =
                CustomBarDrawable(this, Emociones("Muy feliz", 0.0f, R.color.mustard, veryHappy))
            graphHappy.background =
                CustomBarDrawable(this, Emociones("Feliz", 0.0f, R.color.orange, happy))
            graphNeutral.background =
                CustomBarDrawable(this, Emociones("Neutral", 0.0f, R.color.greenie, neutral))
            graphSad.background =
                CustomBarDrawable(this, Emociones("Triste", 0.0f, R.color.blue, sad))
            graphVerySad.background =
                CustomBarDrawable(this, Emociones("Muy triste", 0.0f, R.color.deepBlue, verySad))
        } else {
            actualizarGrafica()
            iconoMayoria()
        }

        guardarButton.setOnClickListener {
            guardar()
        }

        veryHappyButton.setOnClickListener {
            veryHappy++
            iconoMayoria()
            actualizarGrafica()
        }

        happyButton.setOnClickListener {
            happy++
            iconoMayoria()
            actualizarGrafica()
        }

    }}