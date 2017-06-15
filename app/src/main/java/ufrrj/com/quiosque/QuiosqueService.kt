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
 * An [IntentService] subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 *
 *
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
class QuiosqueService : IntentService("QuiosqueService") {

    val QUIOSQUE_URL = "http://academico.ufrrj.br/quiosque/aluno/quiosque.php"

    override fun onHandleIntent(intent: Intent?) {
        if (intent != null) {
            val action = intent.action
            if (ACTION_LOGIN == action) {
                val matricula = intent.getStringExtra(EXTRA_MATRICULA)
                val senha = intent.getStringExtra(EXTRA_SENHA)
                handleActionLogin(matricula, senha)
            } else if (ACTION_BAZ == action) {
                val param1 = intent.getStringExtra(EXTRA_MATRICULA)
                val param2 = intent.getStringExtra(EXTRA_SENHA)
                handleActionBaz(param1, param2)
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private fun handleActionLogin(matricula: String, senha: String) {
        Log.w("QuiosqueService", "Action Login Called")

        val doc: Document = Jsoup.connect(QUIOSQUE_URL)
                .data("edtIdUs", matricula)
                .data("edtIdSen", senha)
                .data("btnIdOk", "Ok")
                .userAgent("Mozilla")
                .post()

        val noticiasElem: Elements = doc.getElementsByClass("item_noticias_dest")

        for (noticia in noticiasElem){
            Log.w("QuiosqueService", "Nova noticia")
            val notification = NotificationCompat.Builder(this)
                    .setContentTitle("Nova notícia")
                    .setSubText(noticia.text())
                    .setSmallIcon(android.R.color.transparent)
                    .build()

            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(1, notification)
        }


    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private fun handleActionBaz(param1: String, param2: String) {
        // TODO: Handle action Baz
        throw UnsupportedOperationException("Not yet implemented")
    }

    companion object {
        // TODO: Rename actions, choose action names that describe tasks that this
        // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
        private val ACTION_LOGIN = "ufrrj.com.quiosque.action.LOGIN"
        private val ACTION_BAZ = "ufrrj.com.quiosque.action.BAZ"

        // TODO: Rename parameters
        private val EXTRA_MATRICULA = "ufrrj.com.quiosque.extra.MATRICULA"
        private val EXTRA_SENHA = "ufrrj.com.quiosque.extra.SENHA"

        /**
         * Starts this service to perform action Foo with the given parameters. If
         * the service is already performing a task this action will be queued.

         * @see IntentService
         */
        // TODO: Customize helper method
        fun startActionLogin(context: Context, matricula: String, senha: String) {
            val intent = Intent(context, QuiosqueService::class.java)
            intent.action = ACTION_LOGIN
            intent.putExtra(EXTRA_MATRICULA, matricula)
            intent.putExtra(EXTRA_SENHA, senha)
            context.startService(intent)
        }

        /**
         * Starts this service to perform action Baz with the given parameters. If
         * the service is already performing a task this action will be queued.

         * @see IntentService
         */
        // TODO: Customize helper method
        fun startActionBaz(context: Context, param1: String, param2: String) {
            val intent = Intent(context, QuiosqueService::class.java)
            intent.action = ACTION_BAZ
            intent.putExtra(EXTRA_MATRICULA, param1)
            intent.putExtra(EXTRA_SENHA, param2)
            context.startService(intent)
        }
    }
}
