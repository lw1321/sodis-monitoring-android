package de.sodis.monitoring.db.response

import de.sodis.monitoring.db.entity.SurveySection


data class SurveyHeaderResponse (val surveyName: String, val section: SurveySection)
