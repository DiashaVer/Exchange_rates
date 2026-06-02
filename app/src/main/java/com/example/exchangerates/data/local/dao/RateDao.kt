package com.example.exchangerates.data.local.dao

import androidx.room.*
import com.example.exchangerates.data.local.entity.RateEntity
import kotlinx.coroutines.flow.Flow

@Dao //4.1
interface RateDao {
    /*
    Часть 4.2 – ViewModel получает Flow, во фрагменте наблюдает за списком валют.
    Часть 2.5 – Избранные валюты могут быть отфильтрованы через другую таблицу,
    но этот запрос возвращает все валюты для сетки.
    Часть 4.4 – Сортировка может быть добавлена запросом ORDER BY (например, по коду).
*/
    @Query("SELECT * FROM rates")
    fun getAll(): Flow<List<RateEntity>> // 3.3


    /*
        Часть 3.8 – При получении новых данных с сервера обновляем (REPLACE) по id.
        Часть 4.1 – Кэширование текущего курса.
        Часть 1.2 (генерация случайного курса) – если нет сети.
    */
        @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(rate: RateEntity)


    @Update
    suspend fun update(rate: RateEntity)
    /*
        Часть 1.2 (удаление валюты) – удаление локальной записи.
        Также может удалить историю через каскад или отдельный вызов.
    */
    @Delete
    suspend fun delete(rate: RateEntity)
    /*
         Часть 4.1 – Очистка кэша (например, при логауте или принудительном обновлении).
         Используется редко, но может потребоваться для синхронизации.
     */
    @Query("DELETE FROM rates")
    suspend fun clearAll()
}