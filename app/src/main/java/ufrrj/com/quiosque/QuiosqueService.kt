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
import android.R.id.home
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.SharedPreferences




/**
 * Realiza as requisições referentes ao Quiosque paralelamente
 */
class QuiosqueService : IntentService("QuiosqueService") {

    override fun onHandleIntent(intent: Intent?) {
        if (intent != null) {
            val action = intent.action
            if (ACTION_LOGIN == action) {
                val matricula = intent.getStringExtra(EXTRA_MATRICULA)
                val senha = intent.getStringExtra(EXTRA_SENHA)
                handleActionLogin(matricula, senha)
            } else if (ACTION_CHECK_NOVIDADES == action) {
                handleActionCheckNovidades()
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
        if (response.parse().getElementById("frmId") != null){
            openLoginActivity("ufrrj.com.quiosque.action.LOGIN_FAIL")
            return
        }
        // Abrindo arquivo para salvar preferencias
        val settings = getSharedPreferences(COOKIES_FILENAME, 0)
        val editor = settings.edit()

        // Salvado cada cookie para usar em futuras requisições
        for (cookie in response.cookies())
            editor.putString(cookie.key, cookie.value)
        // Checando erros ao salvar
        if (!editor.commit())
            Log.w("QuiosqueService", "Falha ao salvar os cookies")
        // Cecando novidades
        handleActionCheckNovidades()
        // Configurando alarm
        QuiosqueSyncAlarm.setAlarm(this)
    }

    private fun handleActionCheckNovidades() {
        // Recuperando ultimo cookie de login
        val settings = getSharedPreferences(COOKIES_FILENAME, 0)
        // Requisitando HomePage
        val homePage = getHomePage(settings.getAll() as Map<String, String>)
        // Se cookie não for válido abrir Login Activity
        if (homePage.getElementById("frmId") != null){
            openLoginActivity("ufrrj.com.quiosque.action.COOKIE_EXPIRED")
            return
        }

        // Pega a lista de elementos de noticias
        val noticiasElementsList = homePage.getElementsByClass("item_noticias")
        // Percorre a lista
        var id = 1;
        for (noticiaElement in noticiasElementsList){
            createNotification(noticiaElement.text(), id++)
        }

    }

    private fun createNotification(notificationText: String, id: Int){
        val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(this)
                .setContentTitle("Novidades no Quiosque")
                .setContentText(notificationText)
                .setSmallIcon(R.color.colorPrimary)
                .build()

        notificationManager.notify(id, notification)
    }

    private fun openLoginActivity(action: String) {
        val context = getBaseContext()
        val intent = Intent(context, MainActivity::class.java)
        intent.setAction(action)
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK)
        getApplication().startActivity(intent)
    }

    private fun getHomePage(cookies: Map<String, String>):Document {
        return Jsoup.connect(QUIOSQUE_URL)
                .cookies(cookies)
                .userAgent("Mozilla")
                .get()
    }


    companion object {

        private val QUIOSQUE_URL = "http://academico.ufrrj.br/quiosque/aluno/quiosque.php"
        private val COOKIES_FILENAME = "CookiesFile"

        private val ACTION_LOGIN = "ufrrj.com.quiosque.action.LOGIN"
        private val ACTION_CHECK_NOVIDADES = "ufrrj.com.quiosque.action.CHECK_NOVIDADES"

        private val EXTRA_MATRICULA = "ufrrj.com.quiosque.extra.MATRICULA"
        private val EXTRA_SENHA = "ufrrj.com.quiosque.extra.SENHA"

        /*
        *   Static method to do Login action
        **/
        @JvmStatic
        fun startActionLogin(context: Context, matricula: String, senha: String) {
            val intent = Intent(context, QuiosqueService::class.java)
            intent.action = ACTION_LOGIN
            intent.putExtra(EXTRA_MATRICULA, matricula)
            intent.putExtra(EXTRA_SENHA, senha)
            context.startService(intent)
        }

        /*
        *   Static method to check if has novidades
        **/
        @JvmStatic
        fun startActionCheckNovidades(context: Context) {
            val intent = Intent(context, QuiosqueService::class.java)
            intent.action = ACTION_CHECK_NOVIDADES
            context.startService(intent)
        }
    }
}
