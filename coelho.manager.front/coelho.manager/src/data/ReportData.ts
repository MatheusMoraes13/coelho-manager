export interface ReportRequestData {
  archive: File | null;
  clientCircuitDesignation: string;
}

export interface ReportResponseData {
  reportPdf: string;
}