package com.airport.passengers.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * JSON received to create or update a passenger.
 * The annotations are checked by @Valid in the controller BEFORE the service runs;
 * a broken rule gives an HTTP 400 with the field name (GlobalExceptionHandler).
 */
public record PassagerRequest(
        @NotBlank(message = "Le nom est obligatoire")
        @Size(max = 80, message = "80 caractères maximum")
        String nom,

        @NotBlank(message = "Le prénom est obligatoire")
        @Size(max = 80, message = "80 caractères maximum")
        String prenom,

        @NotBlank(message = "L'email est obligatoire")
        @Email(message = "Email invalide")
        @Size(max = 150, message = "150 caractères maximum")
        String email,

        @Pattern(regexp = "^$|^[+0-9 ]{8,20}$", message = "Téléphone invalide (chiffres, espaces et + uniquement)")
        String telephone,

        @Past(message = "La date de naissance doit être dans le passé")
        LocalDate dateNaissance,

        @Size(max = 60, message = "60 caractères maximum")
        String nationalite,

        @Pattern(regexp = "^$|^[A-Z0-9]{6,20}$", message = "Passeport : 6 à 20 lettres majuscules ou chiffres")
        String numeroPasseport
) {
}
