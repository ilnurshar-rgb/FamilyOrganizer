package com.family.organizer

import android.app.Application
import com.family.organizer.data.AppDatabase
import com.family.organizer.data.CalendarEventRepository
import com.family.organizer.data.CategoryRepository
import com.family.organizer.data.FamilyMemberRepository
import com.family.organizer.data.GoalRepository
import com.family.organizer.data.MoneyTransactionRepository
import com.family.organizer.data.SavingsAccountRepository
import com.family.organizer.data.ShoppingRepository
import com.family.organizer.data.TaskRepository
import com.family.organizer.data.WishlistItemRepository
import com.family.organizer.data.auth.AuthRepository
import com.family.organizer.data.family.FamilyCloudRepository
import com.family.organizer.data.family.FamilySession
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Простой ручной DI-контейнер вместо Hilt — сознательный выбор для первой
 * версии: меньше движущихся частей, выше шанс, что сборка в CI пройдёт
 * с первого раза.
 *
 * Все репозитории данных семьи синхронизируются через Firestore (см.
 * data/sync/SyncedRepository) и подписаны на общий [familySession] —
 * когда AuthViewModel узнаёт текущую семью пользователя, все репозитории
 * одновременно включают свои облачные слушатели.
 */
class FamilyOrganizerApp : Application() {

    val database: AppDatabase by lazy { AppDatabase.getInstance(this) }

    // Firebase — аккаунты, членство в семье и синхронизация данных
    val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    val authRepository: AuthRepository by lazy { AuthRepository(firebaseAuth) }
    val familySession: FamilySession by lazy { FamilySession() }
    val familyCloudRepository: FamilyCloudRepository by lazy { FamilyCloudRepository(firestore) }

    val shoppingRepository: ShoppingRepository by lazy {
        ShoppingRepository(database.shoppingItemDao(), firestore, familySession)
    }
    val familyMemberRepository: FamilyMemberRepository by lazy {
        FamilyMemberRepository(database.familyMemberDao(), firestore, familySession)
    }
    val categoryRepository: CategoryRepository by lazy {
        CategoryRepository(database.categoryDao(), firestore, familySession)
    }
    val transactionRepository: MoneyTransactionRepository by lazy {
        MoneyTransactionRepository(database.moneyTransactionDao(), firestore, familySession)
    }
    val savingsAccountRepository: SavingsAccountRepository by lazy {
        SavingsAccountRepository(database.savingsAccountDao(), firestore, familySession)
    }
    val goalRepository: GoalRepository by lazy {
        GoalRepository(database.goalDao(), firestore, familySession)
    }
    val taskRepository: TaskRepository by lazy {
        TaskRepository(database.taskDao(), firestore, familySession)
    }
    val calendarEventRepository: CalendarEventRepository by lazy {
        CalendarEventRepository(database.calendarEventDao(), firestore, familySession)
    }
    val wishlistItemRepository: WishlistItemRepository by lazy {
        WishlistItemRepository(database.wishlistItemDao(), firestore, familySession)
    }
}
