package bangkit.daya.app.qna.ml

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*


/**
 * Interface to load squad dataset. Provide passages for users to choose from & provide questions
 * for autoCompleteTextView.
 */
class LoadDatasetClient(private val context: Context) {
    private lateinit var contents: Array<String?>
    lateinit var titles: Array<String?>
        private set
    lateinit private var questions: Array<Array<String>>
    private fun loadJson() {
        try {
            val `is` = context.assets.open(JSON_DIR)
            val reader = JsonReader(InputStreamReader(`is`))
            val map = Gson().fromJson<HashMap<String, List<List<String>>>>(
                reader,
                HashMap::class.java
            )
            val jsonTitles = map["titles"]!!
            val jsonContents = map["contents"]!!
            val jsonQuestions = map["questions"]!!
            titles = listToArray(jsonTitles)
            contents = listToArray(jsonContents)
            questions = arrayOfNulls(jsonQuestions.size)
            var index = 0
            for (item in jsonQuestions) {
                questions[index++] = item.toTypedArray()
            }
        } catch (ex: IOException) {
            Log.e(TAG, ex.toString())
        }
    }

    fun getContent(index: Int): String? {
        return contents[index]
    }

    fun getQuestions(index: Int): Array<String> {
        return questions[index]
    }

    fun loadDictionary(): Map<String, Int> {
        val dic: MutableMap<String, Int> = HashMap()
        try {
            context.assets.open(DIC_DIR).use { ins ->
                BufferedReader(InputStreamReader(ins)).use { reader ->
                    var index = 0
                    while (reader.ready()) {
                        val key = reader.readLine()
                        dic[key] = index++
                    }
                }
            }
        } catch (ex: IOException) {
            Log.e(TAG, ex.message!!)
        }
        return dic
    }

    companion object {
        private const val TAG = "BertAppDemo"
        private const val JSON_DIR = "qa.json"
        private const val DIC_DIR = "vocab.txt"
        private fun listToArray(list: List<List<String>>): Array<String?> {
            val answer = arrayOfNulls<String>(list.size)
            var index = 0
            for (item in list) {
                answer[index++] = item[0]
            }
            return answer
        }
    }

    init {
        loadJson()
    }
}
