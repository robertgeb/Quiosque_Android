package ufrrj.com.quiosque

import android.app.IntentService
import android.app.NotificationManager
import android.content.Intent
import android.content.Context
import android.support.v4.app.NotificationCompat
import android.util.Log
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.lang.UnsupportedOperationException


/**
 * Realiza as requisições referentes ao Quiosque paralelamente
 */
class QuiosqueService : IntentService("QuiosqueService") {

    // URL base do quiosque
    val QUIOSQUE_URL = "http://academico.ufrrj.br/quiosque/aluno/quiosque.php"

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
        // Fazendo o login e recebendo a resposta
        val doc: Document = Jsoup.connect(QUIOSQUE_URL)
                .data("edtIdUs", matricula)
                .data("edtIdSen", senha)
                .data("btnIdOk", "Ok")
                .userAgent("Mozilla")
                .post()
        // Obtendo gerenciador de notificações
        val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // Variável para alterar a id de cada notificação
        var noticiasNotificationId = 1
        // ArrayList com todos os elementos de notícia
        val noticiasElem = doc.getElementsByClass("item_noticias_dest")
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
        fun startActionLogin(context: Context, matricula: String, senha: String) {
            val intent = Intent(context, QuiosqueService::class.java)
            intent.action = ACTION_LOGIN
            intent.putExtra(EXTRA_MATRICULA, matricula)
            intent.putExtra(EXTRA_SENHA, senha)
            context.startService(intent)
        }
    }
}
