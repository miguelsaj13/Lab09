package gt.uvg.lab09.validation

import gt.uvg.lab09.model.BillingType

fun validateName(value: String): String? {
    val trimmedValue = value.trim()
    val letterCount = trimmedValue.count { it.isLetter() }

    if (letterCount < 3) {
        return "El nombre debe contener al menos 3 letras."
    }

    if (trimmedValue.any { it.isDigit() }) {
        return "El nombre no puede contener números."
    }

    return null
}

fun validateNumber(value: String): String? {
    val trimmedValue = value.trim()

    if (trimmedValue.length != 8 || !trimmedValue.all { it.isDigit() }) {
        return "El número telefónico debe contener exactamente 8 dígitos."
    }

    return null
}

fun validateNit(value: String): String? {
    val trimmedValue = value.trim()

    if (trimmedValue.length < 5 || !trimmedValue.all { it.isDigit() }) {
        return "El NIT debe tener al menos 5 dígitos."
    }

    return null
}

fun validateBusinessName(value: String): String? {
    val trimmedValue = value.trim()

    if (trimmedValue.length < 3) {
        return "La razón social debe tener al menos 3 caracteres."
    }

    return null
}

fun isCheckoutValid(
    name: String,
    number: String,
    billingType: BillingType,
    nit: String,
    businessName: String
): Boolean {
    if (validateName(name) != null) {
        return false
    }

    if (validateNumber(number) != null) {
        return false
    }

    if (billingType == BillingType.INVOICE_WITH_NIT) {
        if (validateNit(nit) != null) {
            return false
        }

        if (validateBusinessName(businessName) != null) {
            return false
        }
    }

    return true
}