package com.mariogc55.retrowave.tiendabarrioinventory.model

import java.io.OutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object GeneradorExcel {

    fun validarRango(inicio: Int?, fin: Int?): String? {
        return when {
            inicio == null || fin == null -> "El rango de fechas es obligatorio"
            inicio > fin -> "La fecha de inicio debe ser menor o igual a la fecha fin"
            else -> null
        }
    }

    fun filtrarPorRango(historial: List<ResumenMensual>, rango: Pair<Int, Int>): List<ResumenMensual> {
        val inicio = rango.first.coerceIn(0, historial.lastIndex)
        val fin = rango.second.coerceIn(inicio, historial.lastIndex)
        return historial.subList(inicio, fin + 1)
    }

    fun crearSolicitud(historial: List<ResumenMensual>, inicio: Int?, fin: Int?): Pair<SolicitudExportacion?, String?> {
        val error = validarRango(inicio, fin)
        if (error != null) return Pair(null, error)
        val rango = Pair(inicio!!, fin!!)
        val filas = filtrarPorRango(historial, rango)
        if (!CalculadoraResumen.hayTransacciones(filas)) {
            return Pair(null, "No hay transacciones en el rango seleccionado")
        }
        val nombre = "reporte_inventoryt_${historial[inicio].mes}_${historial[fin].mes}"
            .lowercase() + "." + FormatoExportacion.EXCEL.extension
        return Pair(SolicitudExportacion(FormatoExportacion.EXCEL, rango, nombre), null)
    }

    fun escribirXlsx(salida: OutputStream, filas: List<ResumenMensual>) {
        ZipOutputStream(salida).use { zip ->
            agregar(zip, "[Content_Types].xml", contentTypes())
            agregar(zip, "_rels/.rels", relsRaiz())
            agregar(zip, "xl/workbook.xml", workbook())
            agregar(zip, "xl/_rels/workbook.xml.rels", relsWorkbook())
            agregar(zip, "xl/styles.xml", estilos())
            agregar(zip, "xl/worksheets/sheet1.xml", hoja(filas))
        }
    }

    private fun agregar(zip: ZipOutputStream, ruta: String, contenido: String) {
        zip.putNextEntry(ZipEntry(ruta))
        zip.write(contenido.toByteArray(Charsets.UTF_8))
        zip.closeEntry()
    }

    private fun hoja(filas: List<ResumenMensual>): String {
        val sb = StringBuilder()
        sb.append("""<?xml version="1.0" encoding="UTF-8" standalone="yes"?>""")
        sb.append("""<worksheet xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main">""")
        sb.append("""<cols><col min="1" max="1" width="12" customWidth="1"/><col min="2" max="5" width="16" customWidth="1"/></cols>""")
        sb.append("<sheetData>")

        val encabezados = listOf("Mes", "Ventas", "Ingresos", "Gastos", "Ganancia")
        sb.append("""<row r="1">""")
        encabezados.forEachIndexed { i, titulo -> sb.append(celdaTexto(1, i, titulo, 1)) }
        sb.append("</row>")

        filas.forEachIndexed { indice, resumen ->
            val r = indice + 2
            sb.append("""<row r="$r">""")
            sb.append(celdaTexto(r, 0, resumen.mes, 0))
            sb.append(celdaNumero(r, 1, resumen.totalVentas.toDouble(), 0))
            sb.append(celdaNumero(r, 2, resumen.totalIngresos, 2))
            sb.append(celdaNumero(r, 3, resumen.totalIngresos - resumen.ganancia, 2))
            sb.append(celdaNumero(r, 4, resumen.ganancia, 2))
            sb.append("</row>")
        }

        val rTotal = filas.size + 2
        sb.append("""<row r="$rTotal">""")
        sb.append(celdaTexto(rTotal, 0, "TOTAL", 1))
        sb.append(celdaNumero(rTotal, 1, CalculadoraResumen.calcularTotalVentas(filas).toDouble(), 1))
        sb.append(celdaNumero(rTotal, 2, CalculadoraResumen.calcularIngresosTotales(filas), 3))
        sb.append(celdaNumero(rTotal, 3, CalculadoraResumen.calcularGastosTotales(filas), 3))
        sb.append(celdaNumero(rTotal, 4, CalculadoraResumen.calcularGananciaTotal(filas), 3))
        sb.append("</row>")

        sb.append("</sheetData></worksheet>")
        return sb.toString()
    }

    private fun referencia(fila: Int, columna: Int): String = "${'A' + columna}$fila"

    private fun celdaTexto(fila: Int, columna: Int, texto: String, estilo: Int): String {
        return """<c r="${referencia(fila, columna)}" t="inlineStr" s="$estilo"><is><t>${escapar(texto)}</t></is></c>"""
    }

    private fun celdaNumero(fila: Int, columna: Int, valor: Double, estilo: Int): String {
        val numero = if (valor % 1.0 == 0.0) valor.toLong().toString() else valor.toString()
        return """<c r="${referencia(fila, columna)}" s="$estilo"><v>$numero</v></c>"""
    }

    private fun escapar(texto: String): String {
        return texto.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;")
    }

    private fun contentTypes(): String =
        """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>""" +
                """<Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">""" +
                """<Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>""" +
                """<Default Extension="xml" ContentType="application/xml"/>""" +
                """<Override PartName="/xl/workbook.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml"/>""" +
                """<Override PartName="/xl/worksheets/sheet1.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml"/>""" +
                """<Override PartName="/xl/styles.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.styles+xml"/>""" +
                """</Types>"""

    private fun relsRaiz(): String =
        """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>""" +
                """<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">""" +
                """<Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="xl/workbook.xml"/>""" +
                """</Relationships>"""

    private fun workbook(): String =
        """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>""" +
                """<workbook xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main" xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships">""" +
                """<sheets><sheet name="Resumen mensual" sheetId="1" r:id="rId1"/></sheets>""" +
                """</workbook>"""

    private fun relsWorkbook(): String =
        """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>""" +
                """<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">""" +
                """<Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet" Target="worksheets/sheet1.xml"/>""" +
                """<Relationship Id="rId2" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles" Target="styles.xml"/>""" +
                """</Relationships>"""

    private fun estilos(): String =
        """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>""" +
                """<styleSheet xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main">""" +
                """<numFmts count="1"><numFmt numFmtId="164" formatCode="&quot;$&quot;#,##0"/></numFmts>""" +
                """<fonts count="2"><font><sz val="11"/><name val="Calibri"/></font><font><b/><sz val="11"/><name val="Calibri"/></font></fonts>""" +
                """<fills count="2"><fill><patternFill patternType="none"/></fill><fill><patternFill patternType="gray125"/></fill></fills>""" +
                """<borders count="1"><border><left/><right/><top/><bottom/><diagonal/></border></borders>""" +
                """<cellStyleXfs count="1"><xf numFmtId="0" fontId="0" fillId="0" borderId="0"/></cellStyleXfs>""" +
                """<cellXfs count="4">""" +
                """<xf numFmtId="0" fontId="0" fillId="0" borderId="0" xfId="0"/>""" +
                """<xf numFmtId="0" fontId="1" fillId="0" borderId="0" xfId="0" applyFont="1"/>""" +
                """<xf numFmtId="164" fontId="0" fillId="0" borderId="0" xfId="0" applyNumberFormat="1"/>""" +
                """<xf numFmtId="164" fontId="1" fillId="0" borderId="0" xfId="0" applyFont="1" applyNumberFormat="1"/>""" +
                """<cellStyles count="1"><cellStyle name="Normal" xfId="0" builtinId="0"/></cellStyles>""" +
                """</cellXfs>""" +
                """</styleSheet>"""
}