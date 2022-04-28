package pl.edu.pwr.timeevidence.exception

import org.springframework.http.HttpStatus
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.AccountExpiredException
import org.springframework.security.authentication.CredentialsExpiredException
import org.springframework.security.authentication.LockedException
import org.springframework.security.core.AuthenticationException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import pl.edu.pwr.timeevidence.dto.BasicResponse
import pl.edu.pwr.timeevidence.dto.FieldError

@ControllerAdvice
class CustomExceptionHandler {
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    @ResponseBody
    fun handleInternalException(e: Exception?): BasicResponse {
        return BasicResponse(e?.message ?: "Unknown application error", false)
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ServerException::class)
    @ResponseBody
    fun handleServerException(e: Exception): BasicResponse {
        return BasicResponse(e.message ?: "", false)
    }

    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    @ResponseBody
    fun handleMethodNotSupportedException(e: HttpRequestMethodNotSupportedException): BasicResponse {
        return BasicResponse(e.message ?: "", false)
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(AuthenticationException::class)
    @ResponseBody
    fun handleAuthenticationException(e: AuthenticationException): BasicResponse {
        if (e is AccountExpiredException || e is CredentialsExpiredException) {
            return BasicResponse("Your credentials expired", false)
        }
        return if (e is LockedException || e.cause is LockedException) {
            BasicResponse("Your account is locked", false)
        } else BasicResponse("Wrong login or password", false)
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseBody
    fun handleMethodNotAllowedException(e: MethodArgumentNotValidException): List<FieldError> {
        val errors: MutableList<FieldError> = ArrayList()
        e.bindingResult.fieldErrors.forEach { errors.add(FieldError(it.field, it.defaultMessage)) }
        return errors
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NumberFormatException::class, MethodArgumentTypeMismatchException::class)
    @ResponseBody
    fun handleNumberFormatException(e: RuntimeException): BasicResponse {
        return BasicResponse("Wrong type", false)
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadRequestException::class)
    @ResponseBody
    fun handleBadRequestException(e: BadRequestException): BasicResponse {
        return BasicResponse(e.message ?: "", false)
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(ForbiddenException::class, AccessDeniedException::class)
    @ResponseBody
    fun handleForbiddenException(e: Exception): BasicResponse {
        return BasicResponse(e.message ?: "", false)
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException::class)
    @ResponseBody
    fun handleNotFoundException(e: NotFoundException): BasicResponse {
        return BasicResponse(e.message ?: "", false)
    }
}