import React, { useState } from 'react';
import axios from 'axios';
import InputBox from '../../components/InputComponent/InputComponent.tsx';
import Button from '@mui/material/Button';
import Typography from '@mui/material/Typography';
import CircularProgress from '@mui/material/CircularProgress';
import Alert from '@mui/material/Alert';
import UploadFileIcon from '@mui/icons-material/UploadFile';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import { useNavigate } from 'react-router-dom';
import './ReportPageStyle.css';

const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

export default function ReportGeneratorPage() {
  const [designation, setDesignation] = useState('');
  const [file, setFile] = useState<File | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);

  const [refreshLoading, setRefreshLoading] = useState(false);
  const [refreshResult, setRefreshResult] = useState<string | null>(null);

  const navigate = useNavigate();

  const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    if (event.target.files && event.target.files[0]) {
      setFile(event.target.files[0]);
    }
  };

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();
    setIsLoading(true);
    setError(null);
    setSuccessMessage(null);

    const formData = new FormData();
    if (file) {
      formData.append('archive', file);
    }
    formData.append('clientCircuitDesignation', designation);

    try {
      const response = await axios.post(
        `${API_BASE_URL}/generate-report/isp`,
        formData,
        {
          headers: {
            'Content-Type': 'multipart/form-data',
          },
          responseType: 'blob',
        }
      );

      const url = window.URL.createObjectURL(response.data);
      const a = document.createElement('a');
      a.style.display = 'none';
      a.href = url;
      a.download = `report-${designation}-${new Date().toISOString().split('T')[0]}.pdf`;
      document.body.appendChild(a);
      a.click();
      window.URL.revokeObjectURL(url);
      document.body.removeChild(a);

      setSuccessMessage('Relatório gerado e download iniciado com sucesso!');
    } catch (err: any) {
      let errorMsg = 'Erro inesperado ao gerar relatório.';
      if (err?.response?.data instanceof Blob) {
        try {
          const text = await err.response.data.text();
          errorMsg = text;
        } catch {
          // fallback
        }
      } else if (err?.message) {
        errorMsg = err.message;
      }
      setError(errorMsg);
    } finally {
      setIsLoading(false);
    }
  };

  const handleRefreshCircuits = async () => {
    setRefreshLoading(true);
    setRefreshResult(null);
    try {
      await axios.get(`${API_BASE_URL}/circuits/refresh`);
      setRefreshResult('Circuitos atualizados com sucesso!');
    } catch (error: any) {
      setRefreshResult('Erro ao atualizar circuitos: ' + (error?.message || 'Erro desconhecido'));
    } finally {
      setRefreshLoading(false);
    }
  };

  return (
    <div className="report-page-container">
      <form className="report-form" onSubmit={handleSubmit}>
        <Button
          variant="text"
          startIcon={<ArrowBackIcon />}
          onClick={() => navigate('/')}
          className="report-back-btn"
          style={{ alignSelf: 'flex-start', marginBottom: '0.5rem' }}
        >
          Retornar ao menu inicial
        </Button>

        <Typography variant="h4" component="h1" className="report-title">
          Gerar Relatório de Cliente
        </Typography>

        <InputBox
          label="Designação do Circuito"
          placeholder="Digite a designação do cliente"
          value={designation}
          onChange={(e) => setDesignation(e.target.value)}
        />

        <Button
          variant="outlined"
          component="label"
          startIcon={<UploadFileIcon />}
          className="report-upload-btn"
        >
          {file ? `Selecionado: ${file?.name}` : 'Enviar imagem de tráfego'}
          <input
            type="file"
            hidden
            onChange={handleFileChange}
            accept="image/*"
          />
        </Button>

        <div className="report-actions">
          <Button
            type="submit"
            variant="contained"
            disabled={isLoading || !designation || !file}
            className="report-submit-btn styled-report-btn"
          >
            {isLoading ? (
              <>
                <CircularProgress size={18} color="inherit" style={{ marginRight: 8 }} />
                Gerando relatório...
              </>
            ) : (
              'Gerar Relatório'
            )}
          </Button>
        </div>

        <div className="report-actions">
          <Button
            variant="contained"
            color="secondary"
            onClick={handleRefreshCircuits}
            disabled={refreshLoading}
            className="report-refresh-btn"
          >
            {refreshLoading ? (
              <>
                <CircularProgress size={18} color="inherit" style={{ marginRight: 8 }} />
                Atualizando circuitos...
              </>
            ) : (
              'Atualizar circuitos Netbox'
            )}
          </Button>
        </div>
        {refreshResult && (
          <Alert
            severity={refreshResult.startsWith('Erro') ? 'error' : 'success'}
            className="report-alert"
          >
            {refreshResult}
          </Alert>
        )}

        {error && <Alert severity="error" className="report-alert">{error}</Alert>}
        {successMessage && <Alert severity="success" className="report-alert">{successMessage}</Alert>}
      </form>
    </div>
  );
}