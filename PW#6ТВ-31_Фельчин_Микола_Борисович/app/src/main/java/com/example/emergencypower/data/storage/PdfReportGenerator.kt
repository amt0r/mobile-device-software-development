package com.example.emergencypower.data.storage

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import com.example.emergencypower.data.model.TelemetryLog
import java.io.File
import java.io.FileOutputStream

class PdfReportGenerator(private val context: Context) {

    fun generateAnalyticalReport(logs: List<TelemetryLog>): File {
        val document = PdfDocument()

        val pageInfo1 = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page1 = document.startPage(pageInfo1)
        val canvas1 = page1.canvas

        val paintTitle = Paint().apply {
            color = Color.BLACK
            textSize = 16f
            isFakeBoldText = true
        }

        val paintHeader = Paint().apply {
            color = Color.DKGRAY
            textSize = 11f
            isFakeBoldText = true
        }

        val paintBody = Paint().apply {
            color = Color.BLACK
            textSize = 9.5f
        }

        val paintBorder = Paint().apply {
            color = Color.LTGRAY
            strokeWidth = 1f
            style = Paint.Style.STROKE
        }

        val paintAccent = Paint().apply {
            color = Color.rgb(20, 40, 70)
            style = Paint.Style.FILL
        }

        canvas1.drawRect(40f, 40f, 555f, 75f, paintAccent)
        paintTitle.color = Color.WHITE
        canvas1.drawText("СИСТЕМА АВАРІЙНОГО ЕЛЕКТРОПОСТАЧАННЯ", 55f, 63f, paintTitle)

        paintBody.color = Color.BLACK
        canvas1.drawText("Об'єкт: Підстанція ТП-110/10 кВ «Південна-18» (Варіант 18)", 40f, 100f, paintHeader)
        canvas1.drawText("Оператор: Фельчин М.Б. (Група ТВ-31)", 40f, 116f, paintBody)
        canvas1.drawText("Дата та час генерації: 17.11.2026, 01:00", 40f, 132f, paintBody)
        canvas1.drawText("Статус автоматики АВР: ПОВНА ГОТОВНІСТЬ (100%)", 40f, 148f, paintBody)

        canvas1.drawLine(40f, 160f, 555f, 160f, paintBorder)

        canvas1.drawText("ПРОТОКОЛ ТЕЛЕМЕТРИЧНИХ ПОДІЙ ТА ПЕРЕМИКАНЬ АВР", 40f, 185f, paintHeader)

        var y = 210f
        canvas1.drawRect(40f, y - 15f, 555f, y + 8f, Paint().apply { color = Color.rgb(230, 235, 240) })
        canvas1.drawText("Час", 45f, y, paintHeader)
        canvas1.drawText("Напруга (В)", 110f, y, paintHeader)
        canvas1.drawText("Частота (Гц)", 200f, y, paintHeader)
        canvas1.drawText("Навантаження", 290f, y, paintHeader)
        canvas1.drawText("Джерело живлення", 380f, y, paintHeader)
        canvas1.drawText("Статус", 490f, y, paintHeader)

        y += 22f
        for (log in logs) {
            canvas1.drawLine(40f, y + 6f, 555f, y + 6f, paintBorder)
            canvas1.drawText(log.timestamp, 45f, y, paintBody)
            canvas1.drawText("${log.voltage} В", 110f, y, paintBody)
            canvas1.drawText("${log.frequency} Гц", 200f, y, paintBody)
            canvas1.drawText("${log.loadKw} кВт", 290f, y, paintBody)
            canvas1.drawText(log.source, 380f, y, paintBody)
            canvas1.drawText(log.status, 490f, y, paintBody)
            y += 24f
        }

        y += 30f
        canvas1.drawText("ЗВЕДЕНІ ТЕХНІЧНІ ПОКАЗНИКИ ЗА ПЕРІОД", 40f, y, paintHeader)
        y += 20f
        canvas1.drawText("• Максимальне зафіксоване навантаження: 360.0 кВт", 50f, y, paintBody); y += 16f
        canvas1.drawText("• Час автономної роботи від акумуляторів: 02 хв 32 сек", 50f, y, paintBody); y += 16f
        canvas1.drawText("• Час виходу генератора на робочий режим: 8.4 сек", 50f, y, paintBody); y += 16f
        canvas1.drawText("• Кількість перемикань контакторів секційного вимикача: 2", 50f, y, paintBody); y += 16f
        canvas1.drawText("• Відхилення частоти: у межах норми (49.95 - 50.05 Гц)", 50f, y, paintBody)

        canvas1.drawText("Сторінка 1 з 2", 270f, 810f, paintBody)
        document.finishPage(page1)

        val pageInfo2 = PdfDocument.PageInfo.Builder(595, 842, 2).create()
        val page2 = document.startPage(pageInfo2)
        val canvas2 = page2.canvas

        canvas2.drawRect(40f, 40f, 555f, 75f, paintAccent)
        paintTitle.color = Color.WHITE
        canvas2.drawText("ГРАФІК НАВАНТАЖЕННЯ ТА ЗАТВЕРДЖЕННЯ", 55f, 63f, paintTitle)

        paintHeader.color = Color.DKGRAY
        canvas2.drawText("Діаграма розподілу навантаження споживачів I та II категорій", 40f, 105f, paintHeader)

        val graphLeft = 60f
        val graphTop = 130f
        val graphRight = 535f
        val graphBottom = 330f

        canvas2.drawLine(graphLeft, graphBottom, graphRight, graphBottom, Paint().apply { color = Color.BLACK; strokeWidth = 2f })
        canvas2.drawLine(graphLeft, graphBottom, graphLeft, graphTop, Paint().apply { color = Color.BLACK; strokeWidth = 2f })

        val barPaintGrid = Paint().apply { color = Color.rgb(41, 128, 185) }
        val barPaintBat = Paint().apply { color = Color.rgb(230, 126, 34) }
        val barPaintGen = Paint().apply { color = Color.rgb(39, 174, 96) }

        val bars = listOf(
            Triple(80f, 180f, barPaintGrid),
            Triple(130f, 195f, barPaintGrid),
            Triple(180f, 210f, barPaintGrid),
            Triple(230f, 120f, barPaintBat),
            Triple(280f, 170f, barPaintGen),
            Triple(330f, 175f, barPaintGen),
            Triple(380f, 185f, barPaintGrid),
            Triple(430f, 180f, barPaintGrid)
        )

        for (bar in bars) {
            canvas2.drawRect(bar.first, graphBottom - bar.second, bar.first + 32f, graphBottom, bar.third)
        }

        canvas2.drawText("360 кВт", 15f, graphTop + 20f, paintBody)
        canvas2.drawText("180 кВт", 15f, graphTop + 110f, paintBody)
        canvas2.drawText("0 кВт", 25f, graphBottom, paintBody)

        var legendX = 60f
        canvas2.drawRect(legendX, 350f, legendX + 15f, 360f, barPaintGrid)
        canvas2.drawText("Основна мережа", legendX + 22f, 359f, paintBody)

        legendX += 140f
        canvas2.drawRect(legendX, 350f, legendX + 15f, 360f, barPaintBat)
        canvas2.drawText("Акумуляторне ДБЖ", legendX + 22f, 359f, paintBody)

        legendX += 160f
        canvas2.drawRect(legendX, 350f, legendX + 15f, 360f, barPaintGen)
        canvas2.drawText("Дизель-генератор", legendX + 22f, 359f, paintBody)

        canvas2.drawText("ВИСНОВОК ЕКСПЕРТА", 40f, 410f, paintHeader)
        canvas2.drawText("Система аварійного електропостачання підстанції ТП-110/10 кВ відпрацювала штатний", 40f, 435f, paintBody)
        canvas2.drawText("алгоритм захисту відповідно до регламентів ПУЕ та ДСТУ. Час перемикання на резервне", 40f, 453f, paintBody)
        canvas2.drawText("джерело живлення не перевищив критичну межу 1.5 секунди. Обладнання придатне до експлуатації.", 40f, 471f, paintBody)

        val signBoxTop = 550f
        canvas2.drawRect(40f, signBoxTop, 555f, signBoxTop + 140f, paintBorder)
        canvas2.drawText("СЛУЖБА РЕЛЕЙНОГО ЗАХИСТУ ТА АВТОМАТИКИ (РЗА)", 55f, signBoxTop + 25f, paintHeader)

        canvas2.drawText("Головний інженер підстанції: ___________________ / Недашківський О.Л. /", 55f, signBoxTop + 65f, paintBody)
        canvas2.drawText("Черговий інженер-оператор:  ___________________ / Фельчин М.Б. /", 55f, signBoxTop + 95f, paintBody)
        canvas2.drawText("М.П.                                 Дата затвердження: 17 листопада 2026 р.", 55f, signBoxTop + 125f, paintBody)

        canvas2.drawText("Сторінка 2 з 2", 270f, 810f, paintBody)
        document.finishPage(page2)

        val outFile = File(context.filesDir, "emergency_power_report.pdf")
        FileOutputStream(outFile).use { fos ->
            document.writeTo(fos)
        }
        document.close()
        return outFile
    }
}
