import axios, { AxiosError } from "axios";
import type { ReportRequestData } from "../data/ReportData";


const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

const apiClient = axios.create({
  baseURL: API_BASE_URL,
});

export const generateReport = async (
  { archive, clientCircuitDesignation }: ReportRequestData
): Promise<Blob> => {
  const formData = new FormData();

  if (archive) {
    formData.append('trafficImage', archive);
  }
  formData.append('clientDesignation', clientCircuitDesignation);

  console.log("Sending report generation request for:", clientCircuitDesignation);

  try {
    const response = await apiClient.post(
      '/reports/gen-report-isp',
      formData,
      {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
        responseType: 'blob',
      }
    );

    console.log("Report generated successfully.");
    return new Blob([response.data], { type: response.headers['content-type'] });

  } catch (error) {
    console.error('Failed to generate report:', error);

    let errorMessage = 'Communication with the server failed.';

    if (axios.isAxiosError(error) && error.response) {
      try {
        const errorBlob = error.response.data as Blob;
        const errorText = await errorBlob.text();
        
        try {
            const errorJson = JSON.parse(errorText);
            errorMessage = errorJson.message || errorText;
        } catch (jsonError) {
            errorMessage = errorText || 'An unknown server error occurred.';
        }

      } catch (parseError) {
        console.error('Failed to parse error response:', parseError);
        errorMessage = 'An unknown server error occurred while parsing the response.';
      }
    } else if ((error as any)?.response?.data instanceof Blob) {
      try {
        const text = await (error as any).response.data.text();
        errorMessage = text;
      } catch {
        // fallback
      }
    } else if ((error as any)?.message) {
      errorMessage = (error as any).message;
    }

    throw new Error(errorMessage);
  }
};


