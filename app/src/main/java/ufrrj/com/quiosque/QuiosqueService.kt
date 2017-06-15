package ufrrj.com.quiosque

import android.app.IntentService
import android.app.NotificationManager
import android.content.Intent
import android.content.Context
import android.support.v4.app.NotificationCompat
import android.util.Log
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.lang.UnsupportedOperationException
import android.R.id.edit
import android.content.SharedPreferences




/**
 * Realiza as requisições referentes ao Quiosque paralelamente
 */
class QuiosqueService : IntentService("QuiosqueService") {

    // URL base do quiosque
    private val QUIOSQUE_URL = "http://academico.ufrrj.br/quiosque/aluno/quiosque.php"
    private val DATAFILE_NAME = "QuiosqueFile"

    override fun onHandleIntent(intent: Intent?) {
        if (intent != null) {
            val action = intent.action
            if (ACTION_LOGIN == action) {
                val matricula = intent.getStringExtra(EXTRA_MATRICULA)
                val senha = intent.getStringExtra(EXTRA_SENHA)
                handleActionLogin(matricula, senha)
            }
        }
    }

    /**
     * Realiza o Login com a matricula e a senha
     */
    private fun handleActionLogin(matricula: String, senha: String) {
        // Fazendo a conexão com os dados e recebendo a resposta
        val response: Connection.Response = Jsoup.connect(QUIOSQUE_URL)
                .data("edtIdUs", matricula)
                .data("edtIdSen", senha)
                .data("btnIdOk", "Ok")
                .userAgent("Mozilla")
                .method(Connection.Method.POST)
                .execute()

        // Abrindo arquivo para salvar preferencias
        val settings = getSharedPreferences(DATAFILE_NAME, 0)
        val editor = settings.edit()

        // Salvado cada cookie para usar em futuras requisições
        for (cookie in response.cookies())
            editor.putString(cookie.key, cookie.value)

        if (!editor.commit())
            Log.w("QuiosqueService", "Falha ao salvar os cookies")
        
        // Analisando e salvando dados da pagina inicial
        saveHomePage(response.parse())
    }

    /**
     *  Analisa os dados da página inicial
     */
    private fun saveHomePage(homePage: Document) {
        // Obtendo gerenciador de notificações
        val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // Variável para alterar a id de cada notificação
        var noticiasNotificationId = 1
        // ArrayList com todos os elementos de notícia
        val noticiasElem = homePage.getElementsByClass("item_noticias_dest")
        // Loop pelas notícias
        for (noticia in noticiasElem){
            // Criando notificação
            val notification = NotificationCompat.Builder(this)
                    .setContentTitle("Nova notícia")
                    .setContentText(noticia.text())
                    .setSmallIcon(R.color.colorPrimary)
                    .build()
            // Enviando notificação e alterando ID da próxima notificação
            notificationManager.notify(noticiasNotificationId++, notification)
        }
    }

    companion object {

        private val ACTION_LOGIN = "ufrrj.com.quiosque.action.LOGIN"

        private val EXTRA_MATRICULA = "ufrrj.com.quiosque.extra.MATRICULA"
        private val EXTRA_SENHA = "ufrrj.com.quiosque.extra.SENHA"

        /*
        *   Static method to do Login
        **/
        @JvmStatic
        fun startActionLogin(context: Context, matricula: String, senha: String) {
            val intent = Intent(context, QuiosqueService::class.java)
            intent.action = ACTION_LOGIN
            intent.putExtra(EXTRA_MATRICULA, matricula)
            intent.putExtra(EXTRA_SENHA, senha)
            context.startService(intent)
        }
    }
}
