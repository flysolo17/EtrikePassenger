package com.flysolo.etrike.screens.transaction

import android.content.Context
import android.media.tv.TvContentRating.createRating
import android.os.CountDownTimer
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.lifecycle.viewModelScope
import com.flysolo.etrike.models.emergency.Emergency
import com.flysolo.etrike.models.emergency.EmergencyStatus
import com.flysolo.etrike.models.emergency.LocationInfo
import com.flysolo.etrike.models.emergency.UserInfo
import com.flysolo.etrike.models.ratings.Ratings
import com.flysolo.etrike.models.reports.Reports
import com.flysolo.etrike.models.transactions.TransactionStatus
import com.flysolo.etrike.repository.auth.AuthRepository
import com.flysolo.etrike.repository.emergency.EmergencyRepository
import com.flysolo.etrike.repository.ratings.RatingsRepository
import com.flysolo.etrike.repository.reports.ReportRepository
import com.flysolo.etrike.repository.transactions.TransactionRepository
import com.flysolo.etrike.repository.wallet.WalletRepository
import com.flysolo.etrike.utils.UiState
import com.flysolo.etrike.utils.generateRandomString
import com.flysolo.etrike.utils.shortToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val authRepository: AuthRepository,
    private val ratingsRepository: RatingsRepository,
    private val emergencyRepository: EmergencyRepository,
    private val reportRepository: ReportRepository,
    private val walletRepository : WalletRepository
) : ViewModel() {
    var state by mutableStateOf(TransactionState())
        private set

    fun events(e : TransactionEvents) {
        when(e) {
            is TransactionEvents.OnGetTransactionByID -> getTransactions(e.id)
            is TransactionEvents.OnSetUser -> state = state.copy(
                user = e.user
            )

            is TransactionEvents.OnGetDriverInfo -> getDriverInfo(e.id)

            is TransactionEvents.OnCancelTrip -> cancel(e.transactionID)
            TransactionEvents.OnStartTimer -> startTimer()
            is TransactionEvents.OnStartFindingDriver -> startFindingDriver(e.id)
            is TransactionEvents.OnFindingDriverFailed -> failed(e.id)
            is TransactionEvents.AcceptDriver -> acceptDriver(e.id)
            is TransactionEvents.DeclineDriver -> declineDriver(e.id)
            is TransactionEvents.OnMarkAsCompleted -> markAsComplete(e.id)
            is TransactionEvents.OnGetRatings -> getRatings(e.id)
            is TransactionEvents.OnCreateRatings -> createRating(e.ratings)
            is TransactionEvents.OnGetEmergency -> getEmergency(e.id)
            is TransactionEvents.OnUpdateEmergencyLocation -> updateLocation(e.id,e.location)
            is TransactionEvents.OnUpdateEmergencyStatus -> updateStatus(e.id,e.status)
            is TransactionEvents.OnCreateEmergency -> createEmergency(e.id)
            is TransactionEvents.OnUpdateCurrentLocation -> state = state.copy(
                currentLocation = e.location
            )

            is TransactionEvents.OnSubmitReport -> submitReport(e.issues,e.details,e.context)
            is TransactionEvents.OnWalletScanned -> getWalletScanned(e.id)
            is TransactionEvents.OnPay -> pay(e.myID,
                e.driverID,
                e.transactionID,
                e.amount)
        }
    }

    private fun pay(myID: String, driverID: String, transactionID: String, amount: Double) {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            walletRepository.pay(
                myID, driverID, transactionID, amount
            ).onSuccess {
                events(TransactionEvents.OnMarkAsCompleted(transactionID))
               state = state.copy(
                   isLoading = false
               )
            }.onFailure {
                state = state.copy(
                    isLoading = false,
                    errors = it.message
                )
            }
        }
    }

    private fun getWalletScanned(id: String) {
        viewModelScope.launch {
            walletRepository.getMyWallet(id) {
                val current = state.walletScanned
                state = when (it) {
                    is UiState.Error -> {
                        Log.d("WalletState", "Error: ${it.message}")
                        state.copy(walletScanned = current.copy(isLoading = false, errors = it.message))
                    }
                    UiState.Loading -> {
                        Log.d("WalletState", "Loading Started")
                        state.copy(walletScanned = current.copy(isLoading = true, errors = null))
                    }
                    is UiState.Success -> {
                        Log.d("WalletState", "Success: Wallet data received")
                        state.copy(walletScanned = current.copy(isLoading = false, errors = null, wallet = it.data))
                    }
                }
            }
        }
    }

    private fun submitReport(issues: List<String>, details: String,context: Context) {
        val driver = com.flysolo.etrike.models.emergency.DriverInfo(
            id = state.driver?.id,
            name = state.driver?.name,
            profile = state.driver?.profile,
            franchiseNumber = state.transactions?.franchiseID,
            phone = state.driver?.phone
        )
        val passenger = UserInfo(
            id = state.user?.id,
            name = state.user?.name,
            profile = state.user?.profile,
            phone = state.user?.phone
        )
        val report = Reports(
            id = generateRandomString(8),
            transactionID = state.transactions?.id,
            driver = driver,
            passenger = passenger,
            issues = issues,
            details = details,
        )
        viewModelScope.launch {
            reportRepository.submitReport(report).onSuccess {
                context.shortToast(it)
            }
        }
    }

    private fun createEmergency(id: String) {
        state.transactions?.let {
            val emergency = Emergency(
                transactionID = id,
                driverInfo = com.flysolo.etrike.models.emergency.DriverInfo(
                    id = it.driverID,
                    name = state.driver?.name,
                    profile = state.driver?.profile,
                    franchiseNumber = it.franchiseID,
                    phone = state.driver?.phone
                ),
                passengerInfo = UserInfo(
                    id = state.user?.id,
                    name = state.user?.name,
                    profile = state.user?.profile,
                    phone = state.user?.phone
                ),
                location = LocationInfo(
                    latitude = state.currentLocation?.latitude ?: 0.00,
                    longitude = state.currentLocation?.longitude ?: 0.00
                )
            )
            viewModelScope.launch {
                emergencyRepository.createEmergency(emergency)
            }
        }

    }

    private fun updateStatus(id: String, status: EmergencyStatus) {
        viewModelScope.launch {
            emergencyRepository.updateEmergencyStatus(
                id,
                status
            )
        }
    }

    private fun updateLocation(id: String, location: LocationInfo) {
        viewModelScope.launch {
            emergencyRepository.updateEmergencyLocation(
                id,
                locationInfo = location
            )
        }
    }

    private fun getEmergency(id: String) {
        viewModelScope.launch {
            emergencyRepository.getEmergencyByTransaction(id) {
                if (it is UiState.Success) {
                    state = state.copy(
                        emergency = it.data
                    )
                }
            }
        }
    }

    private fun createRating(ratings: Ratings) {
        viewModelScope.launch {
            ratingsRepository.addRating(ratings)
        }
    }

    private fun getRatings(id: String) {
        viewModelScope.launch {
            ratingsRepository.getRatingByTransactionID(id) {
                if (it is UiState.Success) {
                    val data = it.data.getOrNull(0)
                    state  = state.copy(
                        ratings = data
                    )
                }
            }
        }
    }

    private fun markAsComplete(id: String) {
        viewModelScope.launch {
            transactionRepository.markAsCompleted(id).onSuccess {

            }
        }

    }

    private fun failed(id: String) {
        viewModelScope.launch {
            transactionRepository.markAsFailed(id).onSuccess {

            }
        }
    }

    private fun startFindingDriver(id: String) {
        viewModelScope.launch {
            transactionRepository.markAsPending(id).onSuccess {
                events(TransactionEvents.OnStartTimer)
            }
        }
    }

    private fun startTimer() {
        state = state.copy(
            timer = 180000,
            isTimerRunning = true
        )

        val timer = object : CountDownTimer(state.timer.toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                state = state.copy(
                    timer = millisUntilFinished.toInt()
                )
                Log.d("timer",state.timer.toString())
                if (state.transactions?.status !== TransactionStatus.PENDING) {
                    cancel()
                    state = state.copy(
                        timer = 0,
                        isTimerRunning = false
                    )
                }
            }

            override fun onFinish() {
                state = state.copy(
                    timer = 0,
                    isTimerRunning = false
                )
                events(TransactionEvents.OnFindingDriverFailed(state.transactions?.id?: ""))
            }
        }
        timer.start()
    }


    private fun getDriverInfo(id: String) {
        viewModelScope.launch {

            authRepository.getUserByID(id) {
                state = when(it) {
                    is UiState.Error -> state.copy(
                        isGettingDriverInfo = false,
                        driverError = it.message
                    )

                    UiState.Loading -> state.copy(
                        isGettingDriverInfo = false,
                        driverError = null
                    )

                    is UiState.Success -> state.copy(
                        isGettingDriverInfo = false,
                        driverError = null,
                        driver = it.data
                    )
                }
            }
        }
    }

    private fun getTransactions(id: String) {
        viewModelScope.launch {

            transactionRepository.getTransactionByID(id) { it ->

                when(it) {
                    is UiState.Error ->  state = state.copy(
                        isLoading = false,
                        errors = it.message
                    )
                    UiState.Loading -> state = state.copy(
                        isLoading = true,
                        errors = null
                    )
                    is UiState.Success -> {
                        if (it.data?.status == TransactionStatus.PENDING) {
                            events(TransactionEvents.OnStartTimer)
                            Log.d("timer","Starting timer")
                        }
                        state = state.copy(
                            isLoading = false,
                            errors = null,
                            transactions = it.data,
                        )
                    }
                }
            }
        }
    }


    private fun cancel(transactionID: String) {
        viewModelScope.launch {
            state = state.copy(
                isLoading = true
            )
            transactionRepository.cancelTrip(transactionID).onSuccess {
                state = state.copy(
                    isLoading = false,
                    messages = it
                )
            }.onFailure {
                state = state.copy(
                    isLoading = false,
                    messages = it.localizedMessage
                )
            }
        }
    }

    private fun acceptDriver(transactionID: String) {
        viewModelScope.launch {
            state = state.copy(
                isAcceptingDriver = true
            )

            transactionRepository.acceptDriver(transactionID).onSuccess {
                state = state.copy(
                    isAcceptingDriver = false,
                    messages = it
                )
            }.onFailure {
                state = state.copy(
                    isAcceptingDriver = false,
                    messages = it.localizedMessage
                )
            }
        }
    }

    private fun declineDriver(transactionID: String) {
        viewModelScope.launch {
            state = state.copy(
                isAcceptingDriver = true
            )
            transactionRepository.declineDriver(transactionID).onSuccess {
                state = state.copy(
                    isDecliningDriver = false,
                    messages = it
                )
            }.onFailure {
                state = state.copy(
                    isDecliningDriver = false,
                    messages = it.localizedMessage
                )
            }
        }
    }
}