package com.family.organizer.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        ShoppingItem::class,
        FamilyMember::class,
        Category::class,
        MoneyTransaction::class,
        SavingsAccount::class,
        Goal::class,
        TaskItem::class,
        CalendarEvent::class,
        WishlistItem::class,
    ],
    version = 3,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun shoppingItemDao(): ShoppingItemDao
    abstract fun familyMemberDao(): FamilyMemberDao
    abstract fun categoryDao(): CategoryDao
    abstract fun moneyTransactionDao(): MoneyTransactionDao
    abstract fun savingsAccountDao(): SavingsAccountDao
    abstract fun goalDao(): GoalDao
    abstract fun taskDao(): TaskDao
    abstract fun calendarEventDao(): CalendarEventDao
    abstract fun wishlistItemDao(): WishlistItemDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }

        private fun buildDatabase(context: Context): AppDatabase =
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "family_organizer.db",
            )
                // Приложение ещё не выпущено — при смене схемы проще пересоздать
                // локальную базу, чем писать миграции для несуществующих пользователей.
                // Начиная с версии 3, id всех сущностей — UUID (String), а не
                // автоинкремент: нужно для синхронизации между устройствами семьи
                // через Firestore (см. data/sync). Стартовые данные семьи (члены,
                // категории, копилка) больше не сеются локально при первом запуске —
                // они создаются один раз в Firestore при создании семьи
                // (см. FamilyCloudRepository.createFamily) и приходят на устройство
                // обычной синхронизацией, одинаково для создателя и для тех, кто
                // присоединился по коду.
                .fallbackToDestructiveMigration()
                .build()
    }
}
