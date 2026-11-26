package com.mariogc55.retrowave.tiendabarrioinventory.model

import android.content.Context
import androidx.room.Database
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase

@Entity(tableName = "gastos_locales")
data class GastoEntity(
    @PrimaryKey(autoGenerate = true) val localId: Long = 0,
    val remoteId: Long? = null,
    val descripcion: String,
    val monto: Double,
    val categoria: String,
    val sincronizado: Boolean = false
) {
    fun toGasto(): Gasto = Gasto(
        id = (remoteId ?: localId).toInt(),
        descripcion = descripcion,
        monto = monto,
        categoria = categoria
    )

    fun toDto(): GastoDto = GastoDto(
        id = remoteId,
        descripcion = descripcion,
        monto = monto,
        categoria = categoria
    )
}

@Dao
interface GastoDao {
    @Query("SELECT * FROM gastos_locales ORDER BY localId DESC")
    suspend fun obtenerTodos(): List<GastoEntity>

    @Query("SELECT * FROM gastos_locales WHERE sincronizado = 0")
    suspend fun obtenerPendientes(): List<GastoEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(gasto: GastoEntity): Long

    @Query("UPDATE gastos_locales SET descripcion = :descripcion, monto = :monto, categoria = :categoria, sincronizado = :sincronizado WHERE localId = :localId OR remoteId = :remoteId")
    suspend fun actualizar(
        localId: Long,
        remoteId: Long?,
        descripcion: String,
        monto: Double,
        categoria: String,
        sincronizado: Boolean
    )

    @Query("DELETE FROM gastos_locales WHERE localId = :id OR remoteId = :id")
    suspend fun eliminarPorId(id: Long)

    @Query("DELETE FROM gastos_locales WHERE sincronizado = 1")
    suspend fun limpiarSincronizados()
}

@Entity(tableName = "ventas_locales")
data class VentaEntity(
    @PrimaryKey(autoGenerate = true) val localId: Long = 0,
    val remoteId: Long? = null,
    val productId: String,
    val productName: String,
    val quantity: Int,
    val unitPrice: Double,
    val totalPrice: Double,
    val createdAt: String,
    val sincronizado: Boolean = false
) {
    fun toDto(): VentaDto = VentaDto(
        id = remoteId,
        productId = productId,
        productName = productName,
        quantity = quantity,
        unitPrice = unitPrice,
        totalPrice = totalPrice,
        createdAt = createdAt
    )
}

@Dao
interface VentaDao {
    @Query("SELECT * FROM ventas_locales ORDER BY localId DESC")
    suspend fun obtenerTodas(): List<VentaEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(venta: VentaEntity): Long

    @Query("DELETE FROM ventas_locales WHERE sincronizado = 1")
    suspend fun limpiarSincronizadas()
}

@Database(
    entities = [UserEntity::class, ProductEntity::class, GastoEntity::class, VentaEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun productDao(): ProductDao
    abstract fun gastoDao(): GastoDao
    abstract fun ventaDao(): VentaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tienda_barrio_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}