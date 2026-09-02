import CNLsTable from "../../components/CNLsTableComponent/CNLsTableComponent";
import OutputCNLBox from "../../components/OutputBoxComponent/OutputMunicipalitiesBoxComponent";
import SearchBoxAutoComponent from "../../components/SearchBocAutoComponent/SearchBoxAutoComponent";
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import { useNavigate } from "react-router-dom";
import type { Municipalities } from "../../data/MunicipalitiesData";
import { useState } from "react";

import './CNLQueryPageStyle.css';
import Button from "@mui/material/Button";

function CNLQueryPage() {
    const [selectedMunicipality, setSelectedMunicipality] = useState<Municipalities | null>(null);
    const navigate = useNavigate();

    return (
        <>
        <Button
                  variant="text"
                  startIcon={<ArrowBackIcon />}
                  onClick={() => navigate('/')}
                  className="report-back-btn"
                  style={{ alignSelf: 'flex-start', marginBottom: '0.5rem' }}
                >
                  Retornar ao menu inicial
        </Button>
        <div className="section-search-container">
            
            <h2>Pesquise por município</h2>
            <SearchBoxAutoComponent
                label="Cidade:"
                placeholder="Digite o nome da cidade"
                value={selectedMunicipality}
                onChange={setSelectedMunicipality} />
            <OutputCNLBox
                municipality={selectedMunicipality}
                label="CNL:"
                placeholder="CNL selecionado" />
        </div>
        <CNLsTable />
        </>
    );
}

export default CNLQueryPage;