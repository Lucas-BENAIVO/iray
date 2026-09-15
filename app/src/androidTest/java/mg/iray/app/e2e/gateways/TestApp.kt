package mg.iray.app.e2e.gateways

import android.app.Application

/**
 * Application de test : évite l'initialisation Firebase (IrayApp) pendant
 * les tests instrumentés, pour garder des tests d'égale sur fakes.
 */
class TestApp : Application()